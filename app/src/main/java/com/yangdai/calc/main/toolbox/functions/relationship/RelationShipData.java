package com.yangdai.calc.main.toolbox.functions.relationship;

/**
 * @author 30415
 */
public class RelationShipData {
    public String[][] getRelationShipDataByMan() {
        return new String[][] {
                {"我", "爸爸", "妈妈", "哥哥", "弟弟", "姐姐", "妹妹", "儿子", "女儿", "妻子", "丈夫", "未知亲戚"},
                {"爸爸", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "我", "妹妹", "妈妈", "", "未知亲戚"},
                {"妈妈", "外公", "外婆", "大舅", "小舅", "大姨", "小姨", "我", "妹妹", "", "爸爸", "未知亲戚"},
                {"哥哥", "爸爸", "妈妈", "哥哥", "我", "姐姐", "妹妹", "侄子", "侄女", "嫂子", "", "未知亲戚"},
                {"弟弟", "爸爸", "妈妈", "我", "弟弟", "姐姐", "妹妹", "侄子", "侄女", "弟妹", "", "未知亲戚"},
                {"姐姐", "爸爸", "妈妈", "哥哥", "我", "姐姐", "妹妹", "外甥", "外甥女", "", "姐夫", "未知亲戚"},
                {"妹妹", "爸爸", "妈妈", "我", "弟弟", "姐姐", "妹妹", "外甥", "外甥女", "", "妹夫", "未知亲戚"},
                {"儿子", "我", "妻子", "儿子", "儿子", "女儿", "女儿", "孙子", "孙女", "儿媳", "", "未知亲戚"},
                {"女儿", "我", "妻子", "儿子", "儿子", "女儿", "女儿", "外孙", "外孙女", "", "女婿", "未知亲戚"},
                {"妻子", "岳父", "岳母", "大舅子", "小舅子", "大姨子", "小姨子", "儿子", "女儿", "", "我", "未知亲戚"},
                {"丈夫", "", "", "", "", "", "", "", "", "", "", "未知亲戚"},
                {"爷爷", "曾祖父", "曾祖母", "伯祖父", "叔祖父", "祖姑母", "祖姑母", "爸爸", "姑妈", "奶奶", "", "未知亲戚"},
                {"奶奶", "曾外祖父", "曾外祖母", "舅公", "舅公", "祖姨母", "祖姨母", "爸爸", "姑妈", "", "爷爷", "未知亲戚"},
                {"伯父", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "堂哥", "堂姐", "伯母", "", "未知亲戚"},
                {"叔叔", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "堂弟", "堂妹", "婶婶", "", "未知亲戚"},
                {"姑妈", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "姑表哥", "姑表姐", "", "姑丈", "未知亲戚"},
                {"外公", "外曾祖父", "外曾祖母", "伯外祖父", "叔外祖父", "姑外祖母", "姑外祖母", "舅舅", "妈妈", "外婆", "", "未知亲戚"},
                {"外婆", "外曾外祖父", "外曾外祖母", "外舅公", "外舅公", "姨外祖母", "姨外祖母", "舅舅", "妈妈", "", "外公", "未知亲戚"},
                {"大舅", "外公", "外婆", "大舅", "舅舅", "大姨", "妈妈", "舅表哥", "舅表姐", "大舅妈", "", "未知亲戚"},
                {"小舅", "外公", "外婆", "舅舅", "小舅", "妈妈", "小姨", "舅表弟", "舅表妹", "小舅妈", "", "未知亲戚"},
                {"舅舅", "外公", "外婆", "大舅", "小舅", "大姨", "小姨", "舅表哥", "舅表姐", "舅妈", "", "未知亲戚"},
                {"大姨", "外公", "外婆", "大舅", "舅舅", "大姨", "妈妈", "姨表哥", "姨表姐", "", "大姨父", "未知亲戚"},
                {"小姨", "外公", "外婆", "舅舅", "小舅", "妈妈", "小姨", "姨表弟", "姨表妹", "", "小姨父", "未知亲戚"},
                {"侄子", "哥哥", "嫂子", "侄子", "侄子", "侄女", "侄女", "侄孙子", "侄孙女", "侄媳", "", "未知亲戚"},
                {"侄女", "哥哥", "嫂子", "侄子", "侄子", "侄女", "侄女", "外侄孙", "外侄孙女", "", "侄女婿", "未知亲戚"},
                {"嫂子", "姻伯父", "姻伯母", "姻兄", "姻弟", "姻姐", "姻妹", "侄子", "侄女", "", "哥哥", "未知亲戚"},
                {"弟妹", "姻叔父", "姻叔母", "姻兄", "姻弟", "姻姐", "姻妹", "侄子", "侄女", "", "弟弟", "未知亲戚"},
                {"外甥", "姐夫", "姐姐", "外甥", "外甥", "外甥女", "外甥女", "外甥孙", "外甥孙女", "外甥媳妇", "", "未知亲戚"},
                {"外甥女", "姐夫", "姐姐", "外甥", "外甥", "外甥女", "外甥女", "外甥孙", "外甥孙女", "", "外甥女婿", "未知亲戚"},
                {"姐夫", "姻世伯", "姻伯母", "姻兄", "姻弟", "姻姐", "姻妹", "外甥", "外甥女", "姐姐", "", "未知亲戚"},
                {"妹夫", "姻世伯", "姻伯母", "姻兄", "姻弟", "姻姐", "姻妹", "外甥", "外甥女", "妹妹", "", "未知亲戚"},
                {"孙子", "儿子", "儿媳", "孙子", "孙子", "孙女", "孙女", "曾孙", "曾孙女", "孙媳", "", "未知亲戚"},
                {"孙女", "儿子", "儿媳", "孙子", "孙子", "孙女", "孙女", "曾外孙", "曾外孙女", "", "孙女婿", "未知亲戚"},
                {"儿媳", "亲家公", "亲家母", "姻侄", "姻侄", "姻侄女", "姻侄女", "孙子", "孙女", "", "儿子", "未知亲戚"},
                {"外孙女", "女婿", "女儿", "外孙", "外孙", "外孙女", "外孙女", "外曾外孙", "外曾外孙女", "", "外孙女婿", "未知亲戚"},
                {"外孙", "女婿", "女儿", "外孙", "外孙", "外孙女", "外孙女", "外曾孙", "外曾孙女", "外孙媳", "", "未知亲戚"},
                {"女婿", "亲家公", "亲家母", "姻侄", "姻侄", "姻侄女", "姻侄女", "外孙", "外孙女", "女儿", "", "未知亲戚"},
                {"岳父", "太岳父", "太岳母", "伯岳", "叔岳", "姑岳母", "姑岳母", "大舅子", "大姨子", "岳母", "", "未知亲戚"},
                {"岳母", "外太岳父", "外太岳母", "舅岳父", "舅岳父", "姨岳母", "姨岳母", "大舅子", "大姨子", "", "岳父", "未知亲戚"},
                {"大舅子", "岳父", "岳母", "大舅子", "小舅子", "大姨子", "妻子", "内侄", "内侄女", "舅嫂", "", "未知亲戚"},
                {"小舅子", "岳父", "岳母", "大舅子", "小舅子", "妻子", "小姨子", "内侄", "内侄女", "舅弟媳", "", "未知亲戚"},
                {"大姨子", "岳父", "岳母", "大舅子", "小舅子", "大姨子", "妻子", "内甥", "姨甥女", "", "大姨夫", "未知亲戚"},
                {"小姨子", "岳父", "岳母", "大舅子", "小舅子", "妻子", "小姨子", "内甥", "姨甥女", "", "小姨夫", "未知亲戚"},
                {"曾祖父", "高祖父", "高祖母", "曾伯祖父", "曾叔祖父", "增祖姑母", "增祖姑母", "爷爷", "祖姑母", "曾祖母", "", "未知亲戚"},
                {"曾祖母", "高外祖父", "高外祖母", "舅曾祖父", "舅曾祖父", "姨曾祖母", "姨曾祖母", "爷爷", "祖姑母", "", "曾祖父", "未知亲戚"},
                {"伯祖父", "曾祖父", "曾祖母", "伯祖父", "爷爷", "祖姑母", "祖姑母", "堂伯", "堂姑", "伯祖母", "", "未知亲戚"},
                {"叔祖父", "曾祖父", "曾祖母", "爷爷", "叔祖父", "祖姑母", "祖姑母", "堂伯", "堂姑", "叔祖母", "", "未知亲戚"},
                {"祖姑母", "曾祖父", "曾祖母", "伯祖父", "爷爷", "祖姑母", "祖姑母", "姑表伯父", "姑表姑母", "", "祖姑父", "未知亲戚"},
                {"曾外祖父", "祖太爷", "祖太太", "伯曾外祖父", "叔曾外祖父", "姑曾外祖母", "姑曾外祖母", "舅公", "奶奶", "曾外祖母", "", "未知亲戚"},
                {"曾外祖母", "祖太姥爷", "祖太姥姥", "舅曾外祖父", "舅曾外祖父", "姨曾外祖母", "姨曾外祖母", "舅公", "奶奶", "", "曾外祖父", "未知亲戚"},
                {"舅公", "曾外祖父", "曾外祖母", "舅公", "舅公", "祖姨母", "奶奶", "舅表伯父", "舅表姑母", "舅婆", "", "未知亲戚"},
                {"祖姨母", "曾外祖父", "曾外祖母", "舅公", "舅公", "祖姨母", "奶奶", "姨表伯父", "姨表姑母", "", "祖姨夫", "未知亲戚"},
                {"堂哥", "伯父", "伯母", "堂哥", "堂弟", "堂姐", "堂妹", "堂侄", "堂侄女", "堂嫂", "", "未知亲戚"},
                {"堂弟", "叔叔", "婶婶", "堂哥", "堂弟", "堂姐", "堂妹", "堂侄", "堂侄女", "堂弟媳", "", "未知亲戚"},
                {"堂姐", "伯父", "伯母", "堂哥", "堂弟", "堂姐", "堂妹", "堂外甥", "堂外甥女", "", "堂姐夫", "未知亲戚"},
                {"堂妹", "叔叔", "婶婶", "堂哥", "堂弟", "堂姐", "堂妹", "堂外甥", "堂外甥女", "", "堂妹夫", "未知亲戚"},
                {"伯母", "姻伯公", "姻伯婆", "姻世伯", "姻世伯", "姻伯母", "姻伯母", "堂哥", "堂姐", "", "伯父", "未知亲戚"},
                {"婶婶", "姻伯公", "姻伯婆", "姻世伯", "姻世伯", "姻伯母", "姻伯母", "堂弟", "堂妹", "", "叔叔", "未知亲戚"},

                {"姑表哥", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑表姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑丈", "", "", "", "", "", "", "", "", "", "", ""},

                {"外曾祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑外祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"外曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾外祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"外舅公", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨外祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"舅表哥", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"大舅妈", "", "", "", "", "", "", "", "", "", "", ""},
                {"小舅妈", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅妈", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表弟", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表妹", "", "", "", "", "", "", "", "", "", "", ""},

                {"姨表哥", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表弟", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表妹", "", "", "", "", "", "", "", "", "", "", ""},
                {"大姨父", "", "", "", "", "", "", "", "", "", "", ""},
                {"小姨父", "", "", "", "", "", "", "", "", "", "", ""},

                {"侄孙子", "", "", "", "", "", "", "", "", "", "", ""},
                {"外侄孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"侄孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外侄孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"侄媳", "", "", "", "", "", "", "", "", "", "", ""},
                {"侄女婿", "", "", "", "", "", "", "", "", "", "", ""},

                {"姻伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻叔父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻伯母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻叔母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻兄", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻弟", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻妹", "", "", "", "", "", "", "", "", "", "", ""},

                {"外甥孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外甥孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"外甥媳妇", "", "", "", "", "", "", "", "", "", "", ""},
                {"外甥女婿", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻世伯", "", "", "", "", "", "", "", "", "", "", ""},

                {"曾孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾外孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾外孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"孙媳", "", "", "", "", "", "", "", "", "", "", ""},
                {"孙女婿", "", "", "", "", "", "", "", "", "", "", ""},

                {"亲家公", "", "", "", "", "", "", "", "", "", "", ""},
                {"亲家母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻侄", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻侄女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾外孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾外孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外孙女婿", "", "", "", "", "", "", "", "", "", "", ""},
                {"外孙媳", "", "", "", "", "", "", "", "", "", "", ""},

                {"太岳父", "", "", "", "", "", "", "", "", "", "", ""},
                {"外太岳父", "", "", "", "", "", "", "", "", "", "", ""},
                {"太岳母", "", "", "", "", "", "", "", "", "", "", ""},
                {"外太岳母", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯岳", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔岳", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅岳父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑岳母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨岳母", "", "", "", "", "", "", "", "", "", "", ""},

                {"内侄", "", "", "", "", "", "", "", "", "", "", ""},
                {"内侄女", "", "", "", "", "", "", "", "", "", "", ""},
                {"内甥", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨甥女", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅嫂", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅弟媳", "", "", "", "", "", "", "", "", "", "", ""},
                {"大姨夫", "", "", "", "", "", "", "", "", "", "", ""},
                {"小姨夫", "", "", "", "", "", "", "", "", "", "", ""},

                {"高祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"高祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"高外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"高外祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾伯祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾叔祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅曾祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"增祖姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨曾祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"堂伯", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂姑", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑表伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑表姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖姑父", "", "", "", "", "", "", "", "", "", "", ""},

                {"祖太爷", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖太太", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖太姥爷", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖太姥姥", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑曾外祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨曾外祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"舅表伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅婆", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖姨夫", "", "", "", "", "", "", "", "", "", "", ""},

                {"堂侄", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂侄女", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂嫂", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂弟媳", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂外甥", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂外甥女", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂姐夫", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂妹夫", "", "", "", "", "", "", "", "", "", "", ""},

                {"姻伯公", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻伯婆", "", "", "", "", "", "", "", "", "", "", ""},

                {"未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚"}
        };
    }

    public String[][] getRelationShipDataByWoman() {
        return new String[][] {
                {"我", "爸爸", "妈妈", "哥哥", "弟弟", "姐姐", "妹妹", "儿子", "女儿", "妻子", "丈夫", "未知亲戚"},
                {"爸爸", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "弟弟", "我", "妈妈", "", "未知亲戚"},
                {"妈妈", "外公", "外婆", "大舅", "小舅", "大姨", "小姨", "弟弟", "我", "", "爸爸", "未知亲戚"},
                {"哥哥", "爸爸", "妈妈", "哥哥", "弟弟", "姐姐", "我", "侄子", "侄女", "嫂子", "", "未知亲戚"},
                {"弟弟", "爸爸", "妈妈", "哥哥", "弟弟", "我", "妹妹", "侄子", "侄女", "弟妹", "", "未知亲戚"},
                {"姐姐", "爸爸", "妈妈", "哥哥", "弟弟", "姐姐", "我", "外甥", "外甥女", "", "姐夫", "未知亲戚"},
                {"妹妹", "爸爸", "妈妈", "哥哥", "弟弟", "我", "妹妹", "外甥", "外甥女", "", "妹夫", "未知亲戚"},
                {"儿子", "丈夫", "我", "儿子", "儿子", "女儿", "女儿", "孙子", "孙女", "儿媳", "", "未知亲戚"},
                {"女儿", "丈夫", "我", "儿子", "儿子", "女儿", "女儿", "外孙", "外孙女", "", "女婿", "未知亲戚"},
                {"妻子", "", "", "", "", "", "", "", "", "", "", "未知亲戚"},
                {"丈夫", "公公", "婆婆", "大伯子", "小叔子", "大姑子", "小姑子", "儿子", "女儿", "我", "", "未知亲戚"},
                {"爷爷", "曾祖父", "曾祖母", "伯祖父", "叔祖父", "祖姑母", "祖姑母", "爸爸", "姑妈", "奶奶", "", "未知亲戚"},
                {"奶奶", "曾外祖父", "曾外祖母", "舅公", "舅公", "祖姨母", "祖姨母", "爸爸", "姑妈", "", "爷爷", "未知亲戚"},
                {"伯父", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "堂哥", "堂姐", "伯母", "", "未知亲戚"},
                {"叔叔", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "堂弟", "堂妹", "婶婶", "", "未知亲戚"},
                {"姑妈", "爷爷", "奶奶", "伯父", "叔叔", "姑妈", "姑妈", "姑表哥", "姑表姐", "", "姑丈", "未知亲戚"},
                {"外公", "外曾祖父", "外曾祖母", "伯外祖父", "叔外祖父", "姑外祖母", "姑外祖母", "舅舅", "妈妈", "外婆", "", "未知亲戚"},
                {"外婆", "外曾外祖父", "外曾外祖母", "外舅公", "外舅公", "姨外祖母", "姨外祖母", "舅舅", "妈妈", "", "外公", "未知亲戚"},
                {"大舅", "外公", "外婆", "大舅", "舅舅", "大姨", "妈妈", "舅表哥", "舅表姐", "大舅妈", "", "未知亲戚"},
                {"小舅", "外公", "外婆", "舅舅", "小舅", "妈妈", "小姨", "舅表弟", "舅表妹", "小舅妈", "", "未知亲戚"},
                {"舅舅", "外公", "外婆", "大舅", "小舅", "大姨", "小姨", "舅表哥", "舅表姐", "舅妈", "", "未知亲戚"},
                {"大姨", "外公", "外婆", "大舅", "舅舅", "大姨", "妈妈", "姨表哥", "姨表姐", "", "大姨父", "未知亲戚"},
                {"小姨", "外公", "外婆", "舅舅", "小舅", "妈妈", "小姨", "姨表弟", "姨表妹", "", "小姨父", "未知亲戚"},
                {"侄子", "哥哥", "嫂子", "侄子", "侄子", "侄女", "侄女", "侄孙子", "侄孙女", "侄媳", "", "未知亲戚"},
                {"侄女", "哥哥", "嫂子", "侄子", "侄子", "侄女", "侄女", "外侄孙", "外侄孙女", "", "侄女婿", "未知亲戚"},
                {"嫂子", "姻伯父", "姻伯母", "姻兄", "姻弟", "姻姐", "姻妹", "侄子", "侄女", "", "哥哥", "未知亲戚"},
                {"弟妹", "姻叔父", "姻叔母", "姻兄", "姻弟", "姻姐", "姻妹", "侄子", "侄女", "", "弟弟", "未知亲戚"},
                {"外甥", "姐夫", "姐姐", "外甥", "外甥", "外甥女", "外甥女", "外甥孙", "外甥孙女", "外甥媳妇", "", "未知亲戚"},
                {"外甥女", "姐夫", "姐姐", "外甥", "外甥", "外甥女", "外甥女", "外甥孙", "外甥孙女", "", "外甥女婿", "未知亲戚"},
                {"姐夫", "姻世伯", "姻伯母", "姻兄", "姻弟", "姻姐", "姻妹", "外甥", "外甥女", "姐姐", "", "未知亲戚"},
                {"妹夫", "姻世伯", "姻伯母", "姻兄", "姻弟", "姻姐", "姻妹", "外甥", "外甥女", "妹妹", "", "未知亲戚"},
                {"孙子", "儿子", "儿媳", "孙子", "孙子", "孙女", "孙女", "曾孙", "曾孙女", "孙媳", "", "未知亲戚"},
                {"孙女", "儿子", "儿媳", "孙子", "孙子", "孙女", "孙女", "曾外孙", "曾外孙女", "", "孙女婿", "未知亲戚"},
                {"儿媳", "亲家公", "亲家母", "姻侄", "姻侄", "姻侄女", "姻侄女", "孙子", "孙女", "", "儿子", "未知亲戚"},
                {"外孙女", "女婿", "女儿", "外孙", "外孙", "外孙女", "外孙女", "外曾外孙", "外曾外孙女", "", "外孙女婿", "未知亲戚"},
                {"外孙", "女婿", "女儿", "外孙", "外孙", "外孙女", "外孙女", "外曾孙", "外曾孙女", "外孙媳", "", "未知亲戚"},
                {"女婿", "亲家公", "亲家母", "姻侄", "姻侄", "姻侄女", "姻侄女", "外孙", "外孙女", "女儿", "", "未知亲戚"},
                {"公公", "祖翁", "祖婆", "伯翁", "叔公", "姑婆", "姑婆", "大伯子", "大姑子", "婆婆", "", "未知亲戚"},
                {"婆婆", "外公", "外婆", "舅公", "舅公", "姨婆", "姨婆", "大伯子", "大姑子", "", "公公", "未知亲戚"},
                {"大伯子", "公公", "婆婆", "大伯子", "丈夫", "大姑子", "小姑子", "婆家侄", "侄女", "大婶子", "", "未知亲戚"},
                {"小叔子", "公公", "婆婆", "丈夫", "小叔子", "大姑子", "小姑子", "婆家侄", "侄女", "小婶子", "", "未知亲戚"},
                {"大姑子", "公公", "婆婆", "大伯子", "丈夫", "大姑子", "小姑子", "婆家甥", "外甥女", "", "大姑夫", "未知亲戚"},
                {"小姑子", "公公", "婆婆", "丈夫", "小叔子", "大姑子", "小姑子", "婆家甥", "外甥女", "", "小姑夫", "未知亲戚"},
                {"曾祖父", "高祖父", "高祖母", "曾伯祖父", "曾叔祖父", "增祖姑母", "增祖姑母", "爷爷", "祖姑母", "曾祖母", "", "未知亲戚"},
                {"曾祖母", "高外祖父", "高外祖母", "舅曾祖父", "舅曾祖父", "姨曾祖母", "姨曾祖母", "爷爷", "祖姑母", "", "曾祖父", "未知亲戚"},
                {"伯祖父", "曾祖父", "曾祖母", "伯祖父", "爷爷", "祖姑母", "祖姑母", "堂伯", "堂姑", "伯祖母", "", "未知亲戚"},
                {"叔祖父", "曾祖父", "曾祖母", "爷爷", "叔祖父", "祖姑母", "祖姑母", "堂伯", "堂姑", "叔祖母", "", "未知亲戚"},
                {"祖姑母", "曾祖父", "曾祖母", "伯祖父", "爷爷", "祖姑母", "祖姑母", "姑表伯父", "姑表姑母", "", "祖姑父", "未知亲戚"},
                {"曾外祖父", "祖太爷", "祖太太", "伯曾外祖父", "叔曾外祖父", "姑曾外祖母", "姑曾外祖母", "舅公", "奶奶", "曾外祖母", "", "未知亲戚"},
                {"曾外祖母", "祖太姥爷", "祖太姥姥", "舅曾外祖父", "舅曾外祖父", "姨曾外祖母", "姨曾外祖母", "舅公", "奶奶", "", "曾外祖父", "未知亲戚"},
                {"舅公", "曾外祖父", "曾外祖母", "舅公", "舅公", "祖姨母", "奶奶", "舅表伯父", "舅表姑母", "舅婆", "", "未知亲戚"},
                {"祖姨母", "曾外祖父", "曾外祖母", "舅公", "舅公", "祖姨母", "奶奶", "姨表伯父", "姨表姑母", "", "祖姨夫", "未知亲戚"},
                {"堂哥", "伯父", "伯母", "堂哥", "堂弟", "堂姐", "堂妹", "堂侄", "堂侄女", "堂嫂", "", "未知亲戚"},
                {"堂弟", "叔叔", "婶婶", "堂哥", "堂弟", "堂姐", "堂妹", "堂侄", "堂侄女", "堂弟媳", "", "未知亲戚"},
                {"堂姐", "伯父", "伯母", "堂哥", "堂弟", "堂姐", "堂妹", "堂外甥", "堂外甥女", "", "堂姐夫", "未知亲戚"},
                {"堂妹", "叔叔", "婶婶", "堂哥", "堂弟", "堂姐", "堂妹", "堂外甥", "堂外甥女", "", "堂妹夫", "未知亲戚"},
                {"伯母", "姻伯公", "姻伯婆", "姻世伯", "姻世伯", "姻伯母", "姻伯母", "堂哥", "堂姐", "", "伯父", "未知亲戚"},
                {"婶婶", "姻伯公", "姻伯婆", "姻世伯", "姻世伯", "姻伯母", "姻伯母", "堂弟", "堂妹", "", "叔叔", "未知亲戚"},

                {"姑表哥", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑表姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑丈", "", "", "", "", "", "", "", "", "", "", ""},

                {"外曾祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑外祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"外曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾外祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"外舅公", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨外祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"舅表哥", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"大舅妈", "", "", "", "", "", "", "", "", "", "", ""},
                {"小舅妈", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅妈", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表弟", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表妹", "", "", "", "", "", "", "", "", "", "", ""},

                {"姨表哥", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表弟", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表妹", "", "", "", "", "", "", "", "", "", "", ""},
                {"大姨父", "", "", "", "", "", "", "", "", "", "", ""},
                {"小姨父", "", "", "", "", "", "", "", "", "", "", ""},

                {"侄孙子", "", "", "", "", "", "", "", "", "", "", ""},
                {"外侄孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"侄孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外侄孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"侄媳", "", "", "", "", "", "", "", "", "", "", ""},
                {"侄女婿", "", "", "", "", "", "", "", "", "", "", ""},

                {"姻伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻叔父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻伯母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻叔母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻兄", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻弟", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻姐", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻妹", "", "", "", "", "", "", "", "", "", "", ""},

                {"外甥孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外甥孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"外甥媳妇", "", "", "", "", "", "", "", "", "", "", ""},
                {"外甥女婿", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻世伯", "", "", "", "", "", "", "", "", "", "", ""},

                {"曾孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾外孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾外孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"孙媳", "", "", "", "", "", "", "", "", "", "", ""},
                {"孙女婿", "", "", "", "", "", "", "", "", "", "", ""},

                {"亲家公", "", "", "", "", "", "", "", "", "", "", ""},
                {"亲家母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻侄", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻侄女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾外孙", "", "", "", "", "", "", "", "", "", "", ""},
                {"外曾外孙女", "", "", "", "", "", "", "", "", "", "", ""},
                {"外孙女婿", "", "", "", "", "", "", "", "", "", "", ""},
                {"外孙媳", "", "", "", "", "", "", "", "", "", "", ""},

                {"祖翁", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖婆", "", "", "", "", "", "", "", "", "", "", ""},
                {"外婆", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯翁", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔公", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅公", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑婆", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨婆", "", "", "", "", "", "", "", "", "", "", ""},

                {"婆家侄", "", "", "", "", "", "", "", "", "", "", ""},
                {"侄女", "", "", "", "", "", "", "", "", "", "", ""},
                {"婆家甥", "", "", "", "", "", "", "", "", "", "", ""},
                {"外甥女", "", "", "", "", "", "", "", "", "", "", ""},
                {"大婶子", "", "", "", "", "", "", "", "", "", "", ""},
                {"小婶子", "", "", "", "", "", "", "", "", "", "", ""},
                {"大姑夫", "", "", "", "", "", "", "", "", "", "", ""},
                {"小姑夫", "", "", "", "", "", "", "", "", "", "", ""},

                {"高祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"高祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"高外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"高外祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾伯祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"曾叔祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅曾祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"增祖姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨曾祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"堂伯", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂姑", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑表伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑表姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖姑父", "", "", "", "", "", "", "", "", "", "", ""},

                {"祖太爷", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖太太", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖太姥爷", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖太姥姥", "", "", "", "", "", "", "", "", "", "", ""},
                {"伯曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"叔曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅曾外祖父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姑曾外祖母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨曾外祖母", "", "", "", "", "", "", "", "", "", "", ""},

                {"舅表伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表伯父", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅表姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"姨表姑母", "", "", "", "", "", "", "", "", "", "", ""},
                {"舅婆", "", "", "", "", "", "", "", "", "", "", ""},
                {"祖姨夫", "", "", "", "", "", "", "", "", "", "", ""},

                {"堂侄", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂侄女", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂嫂", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂弟媳", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂外甥", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂外甥女", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂姐夫", "", "", "", "", "", "", "", "", "", "", ""},
                {"堂妹夫", "", "", "", "", "", "", "", "", "", "", ""},

                {"姻伯公", "", "", "", "", "", "", "", "", "", "", ""},
                {"姻伯婆", "", "", "", "", "", "", "", "", "", "", ""},

                {"未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚", "未知亲戚"}
        };
    }
}

