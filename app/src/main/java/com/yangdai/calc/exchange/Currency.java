package com.yangdai.calc.exchange;

/**
 * @author 30415
 */
public class Currency {
    private final String symbol;
    private final String chineseName;
    private final String englishName;

    public Currency(String symbol, String chineseName, String englishName) {
        this.symbol = symbol;
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }
}

