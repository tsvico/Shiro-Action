package cn.tsvico.common.util

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.awt.color.ColorSpace
import java.lang.StringBuilder
import java.util.*
import com.wf.captcha.SpecCaptcha
import com.wf.captcha.GifCaptcha
import com.wf.captcha.ChineseGifCaptcha
import com.wf.captcha.ChineseCaptcha
import com.wf.captcha.ArithmeticCaptcha
import com.wf.captcha.base.Captcha
import org.springframework.stereotype.Component

/**
 * 验证码工具类
 */
@Component
class CaptchaUtil {
    /**
     * 生成验证码
     * @param width         验证码宽度
     * @param height        验证码高度
     * @param codeCount     验证码个数
     * @param lineCount     干扰线个数
     * @param lineLenght    干扰线长度
     * @return  验证码对象
     */
    fun createCaptcha(width: Int, height: Int, codeCount: Int, lineCount: Int, lineLenght: Int): LocalCaptcha {
        val image = BufferedImage(width, height, ColorSpace.TYPE_Lab)
        val g = image.graphics
        val random = Random()

        // 取颜色区间中较淡的部分
        g.color = getRandColor(200, 250)
        g.fillRect(0, 0, width, height)
        g.font = Font("Times New Roman", Font.PLAIN, 20)
        g.color = getRandColor(160, 200)
        // 干扰线
        for (i in 0 until lineCount) {
            val x = random.nextInt(width)
            val y = random.nextInt(height)
            val xLength = random.nextInt(lineLenght)
            val yLength = random.nextInt(lineLenght)
            g.drawLine(x, y, x + xLength, y + yLength)
        }
        val code = StringBuilder()

        // 生成验证码
        for (i in 0 until codeCount) {
            val rand = random.nextInt(10).toString()
            code.append(rand)
            g.color = Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110))
            g.drawString(rand, width / codeCount * i + width / codeCount / 2, height / 2 + 9)
        }
        g.dispose()
        return LocalCaptcha(code.toString(), image)
    }

    /**
     * 给定范围内获取颜色值
     */
    private fun getRandColor(fc: Int, bc: Int): Color {
        var fcTmp = fc
        var bcTmp = bc
        val random = Random()
        if (fc > 255) {
            fcTmp = 255
        }
        if (bc > 255) {
            bcTmp = 255
        }
        val r = fcTmp + random.nextInt(bcTmp - fcTmp)
        val g = fcTmp + random.nextInt(bcTmp - fcTmp)
        val b = fcTmp + random.nextInt(bcTmp - fcTmp)
        return Color(r, g, b)
    }

    /**
     * 验证码对象
     */
    class LocalCaptcha constructor(val code: String, val image: BufferedImage)

    class LoginCode {
        /**
         * 验证码配置
         */
        var codeType: LoginCodeEnum? = null

        /**
         * 验证码有效期 分钟
         */
        val expiration = 2L

        /**
         * 验证码内容长度
         */
        val length = 3

        /**
         * 验证码宽度
         */
        val width = 140

        /**
         * 验证码高度
         */
        val height = 38

        /**
         * 验证码字体
         */
        val fontName: String? = null

        /**
         * 字体大小
         */
        val fontSize = 25
    }

    /**
     * @date 2021/2/10 19:17
     * @description: 验证码配置枚举
     */
    enum class LoginCodeEnum {
        /**
         * 算数
         */
        arithmetic,

        /**
         * 中文
         */
        chinese,

        /**
         * 中文闪图
         */
        chinese_gif,

        /**
         * 闪图
         */
        gif, spec
    }

    private var loginCode: LoginCode? = null

    /**
     * 获取验证码生产类
     *
     * @return /
     */
    fun getCaptcha(): Captcha? {
        if (Objects.isNull(loginCode)) {
            loginCode = LoginCode()
            if (Objects.isNull(loginCode!!.codeType)) {
                loginCode!!.codeType = LoginCodeEnum.arithmetic
            }
        }
        return switchCaptcha(loginCode!!)
    }

    /**
     * 依据配置信息生产验证码
     *
     * @param loginCode 验证码配置信息
     * @return /
     */
    private fun switchCaptcha(loginCode: LoginCode): Captcha {
        var captcha: Captcha
        synchronized(this) {
            when (loginCode.codeType) {
                LoginCodeEnum.arithmetic -> {
                    // 算术类型 https://gitee.com/whvse/EasyCaptcha
                    captcha = ArithmeticCaptcha(loginCode.width, loginCode.height)
                    // 几位数运算，默认是两位
                    captcha.len = loginCode.length
                }
                LoginCodeEnum.chinese -> {
                    captcha = ChineseCaptcha(loginCode.width, loginCode.height)
                    captcha.len = loginCode.length
                }
                LoginCodeEnum.chinese_gif -> {
                    captcha = ChineseGifCaptcha(loginCode.width, loginCode.height)
                    captcha.len = loginCode.length
                }
                LoginCodeEnum.gif -> {
                    captcha = GifCaptcha(loginCode.width, loginCode.height)
                    captcha.len = loginCode.length
                }
                LoginCodeEnum.spec -> {
                    captcha = SpecCaptcha(loginCode.width, loginCode.height)
                    captcha.len = loginCode.length
                }
                else -> throw Error("验证码配置信息错误！正确配置查看 LoginCodeEnum ")
            }
        }
        if (!loginCode.fontName.isNullOrBlank()) {
            captcha.font = Font(loginCode.fontName, Font.PLAIN, loginCode.fontSize)
        }
        return captcha
    }

}