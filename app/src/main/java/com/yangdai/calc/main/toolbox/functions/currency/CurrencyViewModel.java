package com.yangdai.calc.main.toolbox.functions.currency;

import android.util.Xml;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 30415
 */
public class CurrencyViewModel extends ViewModel {

    private MutableLiveData<HashMap<String, Double>> exchangeRates;
    private static final String ECB_API_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    private ExecutorService executorService;

    public LiveData<HashMap<String, Double>> getExchangeRates() {
        if (exchangeRates == null) {
            exchangeRates = new MutableLiveData<>();
            loadExchangeRates();
        }
        return exchangeRates;
    }

    public void loadExchangeRates() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(() -> {
            try {
                URL url = new URL(ECB_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                InputStream inputStream = connection.getInputStream();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inputStream, null);

                HashMap<String, Double> exchangeRatesMap = parseXml(parser);

                inputStream.close();
                connection.disconnect();

                exchangeRates.postValue(exchangeRatesMap);
            } catch (Exception e) {
                e.printStackTrace();
                exchangeRates.postValue(null);
            }
        });
    }

    private HashMap<String, Double> parseXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        HashMap<String, Double> exchangeRates = new HashMap<>();
        int eventType = parser.getEventType();
        String currency = "";
        double rate = 0.0;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG -> {
                    if ("Cube".equals(tagName) && parser.getAttributeCount() == 2) {
                        currency = parser.getAttributeValue(null, "currency");
                        String rateString = parser.getAttributeValue(null, "rate");
                        rate = Double.parseDouble(rateString);
                    }
                }
                case XmlPullParser.END_TAG -> {
                    if ("Cube".equals(tagName) && !currency.isEmpty()) {
                        exchangeRates.put(currency, rate);
                        currency = "";
                    }
                }
                default -> {
                }
            }
            eventType = parser.next();
        }
        exchangeRates.put("EUR", 1.0);
        return exchangeRates;
    }
}
