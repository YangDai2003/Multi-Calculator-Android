package com.yangdai.calc.utils;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.yangdai.calc.R;

import java.util.Locale;

/**
 * @author 30415
 */
public class TTS implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private Context mContext;
    private TTSInitializationListener initializationListener;

    public boolean ttsCreate(Activity activity, TTSInitializationListener initializationListener) {
        //语音初始化
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        mContext = activity.getApplicationContext();
        this.initializationListener = initializationListener;
        // 创建新的TextToSpeech对象并设置语言
        try {
            textToSpeech = new TextToSpeech(mContext, this);
            return true;
        } catch (Exception exception) {
            textToSpeech = null;
            return false;
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

    public void ttsSpeak(String content) {
        if (textToSpeech != null && !content.isEmpty()) {
            content = content.replace("=", mContext.getString(R.string.equal))
                    .replace("(", mContext.getString(R.string.bracket))
                    .replace(")", mContext.getString(R.string.bracket))
                    .replace("!!", mContext.getString(R.string.double_factorial))
                    .replace("!", mContext.getString(R.string.factorial))
                    .replace("%", mContext.getString(R.string.percentage))
                    .replace("^", mContext.getString(R.string.power))
                    .replace(".", mContext.getString(R.string.point))
                    .replace("+", mContext.getString(R.string.addNum))
                    .replace("-", mContext.getString(R.string.minusNum))
                    .replace("×", mContext.getString(R.string.multiplyNum))
                    .replace("÷", mContext.getString(R.string.divideNum))
                    .replace("asin", mContext.getString(R.string.asin))
                    .replace("acos", mContext.getString(R.string.acos))
                    .replace("atan", mContext.getString(R.string.atan))
                    .replace("acot", mContext.getString(R.string.acot))
                    .replace("sin", mContext.getString(R.string.sin))
                    .replace("cos", mContext.getString(R.string.cos))
                    .replace("tan", mContext.getString(R.string.tan))
                    .replace("cot", mContext.getString(R.string.cot))
                    .replace("log", mContext.getString(R.string.log))
                    .replace("ln", mContext.getString(R.string.ln));
            textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 获取应用语言设置
            Locale appLocale = mContext.getResources().getConfiguration().getLocales().get(0);

            // 设置TextToSpeech的语言为应用语言
            if (textToSpeech != null) {
                int result = textToSpeech.setLanguage(appLocale);
                textToSpeech.setSpeechRate(1.2f);
                textToSpeech.setPitch(0.8f);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech = null;
                    // 语言数据缺失或不支持，无法进行语音播报
                    Log.e("TTS", "Language pack is missing");
                    initializationListener.onTTSInitialized(false);
                }
            } else {
                Log.e("TTS", "TextToSpeech object is null");
                initializationListener.onTTSInitialized(false);
            }
        } else {
            textToSpeech = null;
            // 可能遇到手机内置讯飞tts
            textToSpeech = new TextToSpeech(mContext, i -> {
                if (i == TextToSpeech.SUCCESS) {
                    // 获取应用语言设置
                    Locale appLocale = mContext.getResources().getConfiguration().getLocales().get(0);

                    // 设置TextToSpeech的语言为应用语言
                    if (textToSpeech != null) {
                        int result = textToSpeech.setLanguage(appLocale);
                        textToSpeech.setSpeechRate(1.2f);
                        textToSpeech.setPitch(0.8f);

                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            textToSpeech = null;
                            // 语言数据缺失或不支持，无法进行语音播报
                            Log.e("TTS", "Language pack is missing");
                            initializationListener.onTTSInitialized(false);
                        }
                    } else {
                        Log.e("TTS", "TextToSpeech object is null");
                        initializationListener.onTTSInitialized(false);
                    }
                } else {
                    textToSpeech = null;
                    // 初始化TextToSpeech失败
                    Log.e("TTS", "Failed to initialize TTS");
                    initializationListener.onTTSInitialized(false);
                }
            }, "com.iflytek.speechsuite");
        }
    }
}

