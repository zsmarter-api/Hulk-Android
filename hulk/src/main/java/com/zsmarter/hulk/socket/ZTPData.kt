package com.zsmarter.hulk.socket

import com.zsmarter.hulk.socket.excption.ProtocolException
import com.zsmarter.hulk.socket.ext.toByteArray
import com.zsmarter.hulk.socket.ext.toInt
import com.zsmarter.hulk.socket.util.checkProtocolException
import com.zsmarter.hulk.socket.util.readByteArray
import java.io.InputStream
import java.util.zip.CRC32


/***
 *
 * 协议组装数据：
 *
 * Head   Verify    Version    Type    subType    Ack   Encrypt   Seq     Reversed   DataLen     Data
 * 0x7E  (unit32)  (unit8)  (unit16)  (unit16)  (4bit)  (4bit)  (unit16)  (16byte)   (unit32)  (N byte)
 *
 * Head:  协议头，固定为0x7E
 * Verify: 将Type到Data(包含)所有bytes，采用CRC32算法(Table为IEEE 802.3)求得的unit32值
 * Version: 协议头版本，暂时固定为0x1
 * Type: 协议type
 * SubType: 协议subtype
 * ACK: 协议回复标记，0x0为无需回复;0x1需要回复;0x2为回复
 * Encrypt: 0x0不加密，0x1是SM4_CFB_PKCS7加密，0x2是SM4_CBC_PKCS7加密，0x3是SM4_ECB_PKCS7加密，
 *          0x4是SM4_OFB_PKCS7加密，0x5是AES_CBC_PKCS5加密，0x6是AES_ECB加密，0x7是AES_CFB加密
 * Seq: 命令序列号
 * Reserved: 预留位，待扩展。
 * DataLen: 标记Data的长度，Max:2^20
 * Data: 业务数据，使用UTF-8编码的字符串
 *
 * 除Data以外，其他字段在ByteArray中的总长度为1+4+1+2+2+0.5+0.5+2+16+4=33个字节，Data字段N字节
 */
class ZTPData {

    companion object {
        private const val HEAD = 0x7E
        private const val VERSION = 0x3
        private const val HEAD_LENGTH = 1
        private const val VERIFY_LENGTH = 4
        private const val VERSION_LENGTH = 1
        private const val TYPE_LENGTH = 2
        private const val SUBTYPE_LENGTH = 2
        private const val ACK_ENCRYPT_LENGTH = 1
        private const val SEQ_LENGTH = 2
        private const val REVERSED_LENGTH = 16
        private const val DATA_LEN_LENGTH = 4
        private const val EXCEPT_DATA_LENGTH = HEAD_LENGTH +
                VERIFY_LENGTH +
                VERSION_LENGTH +
                TYPE_LENGTH +
                SUBTYPE_LENGTH +
                ACK_ENCRYPT_LENGTH +
                SEQ_LENGTH +
                REVERSED_LENGTH +
                DATA_LEN_LENGTH

        @JvmStatic
        @Throws(ProtocolException::class)
        fun getZTPDataBytes(ztpData: ZTPData): ByteArray {
            // head长度为1个字节
            val headBytes = ztpData.head.toByteArray(HEAD_LENGTH)
            // version长度为1个字节
            val versionBytes = ztpData.version.toByteArray(VERSION_LENGTH)
            // type长度为2个字节
            val typeBytes = ztpData.type.toByteArray(TYPE_LENGTH)
            // subType长度为2个字节
            val subTypeBytes = ztpData.subType.toByteArray(SUBTYPE_LENGTH)
            // ack长度4位，encrypt长度4位
            val ackByte = ztpData.ack.toByte()
            val encryptByte = ztpData.encrypt.toByte()
            // ack和encrypt一起长度为1个字节
            val ackEncryptBytes = getAckEncryptByte(ackByte, encryptByte, 4)
                .toByteArray(ACK_ENCRYPT_LENGTH)
            // seq长度为2个字节
            val seqBytes = ztpData.seq.toByteArray(SEQ_LENGTH)
            // reversed长度为16个字节
            val reversedBytes = ztpData.reversed.copyOf(REVERSED_LENGTH)
            // dataLen长度为4个字节
            ztpData.dataLen = ztpData.data.size
            val dataLenBytes = ztpData.dataLen.toByteArray(DATA_LEN_LENGTH)
            // data长度不定
            val dataBytes = ztpData.data
            // verify长度为4个字节
            ztpData.verify = getCrc32(
                typeBytes,
                subTypeBytes,
                ackEncryptBytes,
                seqBytes,
                reversedBytes,
                dataLenBytes,
                dataBytes,
            ).toInt()
            val verifyBytes = ztpData.verify.toByteArray(VERIFY_LENGTH)

            return headBytes +
                    verifyBytes +
                    versionBytes +
                    typeBytes +
                    subTypeBytes +
                    ackEncryptBytes +
                    seqBytes +
                    reversedBytes +
                    dataLenBytes +
                    dataBytes
        }

        @JvmStatic
        @Throws(ProtocolException::class)
        fun readFromInputStream(inputStream: InputStream): ZTPData {
            val ztpData = ZTPData()
            // 读取Head+Verify+Version+Type+SubType+Ack+Encrypt+Seq+Reversed+DataLen=33个字节长度
            val bufferedInputStream = inputStream.buffered()
            val buffer = bufferedInputStream.readByteArray(EXCEPT_DATA_LENGTH)
            // 从buffer中取出Head、Verify、Version、Type、SubType、Ack、Encrypt、Seq、Reversed、DataLen
            var position = 0
            ztpData.head = buffer.toInt(position, HEAD_LENGTH)
                .also { checkProtocolException(it == 0x7E) { "received data head must be equal to 0x7E." } }
            position += HEAD_LENGTH
            ztpData.verify = buffer.toInt(position, VERIFY_LENGTH)
            position += VERIFY_LENGTH
            ztpData.version = buffer.toInt(position, VERSION_LENGTH)
            position += VERSION_LENGTH
            ztpData.type = buffer.toInt(position, TYPE_LENGTH)
            position += TYPE_LENGTH
            ztpData.subType = buffer.toInt(position, SUBTYPE_LENGTH)
            position += SUBTYPE_LENGTH
            val ackEncrypt = buffer.toInt(position, ACK_ENCRYPT_LENGTH)
            ztpData.ack = ackEncrypt / 16
            ztpData.encrypt = ackEncrypt % 16
            position += ACK_ENCRYPT_LENGTH
            ztpData.seq = buffer.toInt(position, SEQ_LENGTH)
            position += SEQ_LENGTH
            ztpData.reversed = buffer.sliceArray(position until position + REVERSED_LENGTH)
            position += REVERSED_LENGTH
            // 从exceptDataBytes中取出字段DataLen，以获取Data的长度
            ztpData.dataLen = buffer.toInt(position, DATA_LEN_LENGTH)
            // 读取data
            val dataBuffer = bufferedInputStream.readByteArray(ztpData.dataLen)
            ztpData.data = dataBuffer
            val verified = ztpData.verify == getCrc32(
                buffer.copyOfRange(
                    HEAD_LENGTH + VERIFY_LENGTH + VERSION_LENGTH,
                    EXCEPT_DATA_LENGTH
                ), dataBuffer.copyOf()
            ).toInt()
            checkProtocolException(verified) {
                "received data field verify failed: 'verify = ${ztpData.verify}'"
            }
            return ztpData
        }

        private fun getCrc32(vararg byteArrays: ByteArray): Long {
            val mergedBytes = byteArrays.fold(byteArrayOf()) { acc, byteArray -> acc + byteArray }
            return CRC32().also { it.update(mergedBytes) }.value
        }

        private fun getAckEncryptByte(ackByte: Byte, encryptByte: Byte, bit: Int): Int {
            var num = 0
            val rLength: Int = 8 - bit
            for (i in 0 until rLength) {
                num += ((ackByte.toInt() shr (rLength - 1 - i)) and 0x1) shl (7 - i)
            }
            return num + encryptByte
        }
    }

    /**********************************************************************************************/

    /**
     * 协议数据字段，一般情况下，只需要在外部赋值type、subType、ack、seq、data就可以了
     * 其他字段head、version、encrypt、reversed使用默认值
     * verify和dataLen是根据已有字段生成的验证字段，外部无需赋值
     */
    private var head: Int = HEAD        // 1byte
    private var verify: Int = 0x0      // 4byte
    internal var version: Int = VERSION         // 1byte
    var type: Int = 0x0                // 2byte
    var subType: Int = 0x0             // 2byte
    var ack: Int = 0x0                 // 4bit
    internal var encrypt: Int = 0x2    // 4bit
    var seq: Int = 0x0                 // 2byte
    private var reversed: ByteArray = ByteArray(REVERSED_LENGTH) // 16byte
    private var dataLen: Int = 0x0     // 4byte
    var data: ByteArray = "".toByteArray()                       // N byte

    /**********************************************************************************************/

    val content: String
        get() = "type: $type | " +
                "subType: $subType | " +
                "ack: $ack | " +
                "seq: | $seq " + "| " +
                "encrypt: $encrypt | " +
                "data: ${data.toString(Charsets.UTF_8)}"

    /**********************************************************************************************/

    /**
     * 拷贝一个新的ZTPData对象
     */
    fun copy(): ZTPData {
        return ZTPData().also {
            it.head = this.head
            it.verify = this.verify
            it.version = this.version
            it.type = this.type
            it.subType = this.subType
            it.ack = this.ack
            it.encrypt = this.encrypt
            it.seq = this.seq
            it.reversed = this.reversed
            it.dataLen = this.dataLen
            it.data = this.data
        }
    }
}