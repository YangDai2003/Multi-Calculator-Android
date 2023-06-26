package com.yangdai.calc.utils;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/**
 * @author 30415
 */
public class TTS implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private Context mContext;

    public void ttsCreate(Activity activity) {
        //语音初始化
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        mContext = activity.getApplicationContext();
        // 创建新的TextToSpeech对象并设置语言
        try {
            textToSpeech = new TextToSpeech(mContext, this);
        } catch (Exception ignored) {

        }
    }

    public void ttsDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            // 关闭，释放资源
        }
    }

    public void ttsSpeak(final String content) {
        if (textToSpeech != null) {
            textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 获取应用语言设置
            Locale appLocale = mContext.getResources().getConfiguration().getLocales().get(0);

            // 设置TextToSpeech的语言为应用语言
            int result = textToSpeech.setLanguage(appLocale);
            textToSpeech.setSpeechRate(1.3f);
            textToSpeech.setPitch(0.8f);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech = null;
                // 语言数据缺失或不支持，无法进行语音播报
                Toast.makeText(mContext, "Language pack is missing", Toast.LENGTH_SHORT).show();
            }
        } else {
            textToSpeech = null;
            // 可能遇到手机内置讯飞tts
            textToSpeech = new TextToSpeech(mContext, i -> {
                if (status == TextToSpeech.SUCCESS) {
                    // 获取应用语言设置
                    Locale appLocale = mContext.getResources().getConfiguration().getLocales().get(0);

                    // 设置TextToSpeech的语言为应用语言
                    int result = textToSpeech.setLanguage(appLocale);
                    textToSpeech.setSpeechRate(1.3f);
                    textToSpeech.setPitch(0.8f);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech = null;
                        // 语言数据缺失或不支持，无法进行语音播报
                        Toast.makeText(mContext, "Language pack is missing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    textToSpeech = null;
                    // 初始化TextToSpeech失败
                    Toast.makeText(mContext, "Failed to initialize TTS", Toast.LENGTH_SHORT).show();
                }
            }, "com.iflytek.speechsuite");
        }
    }
}
