package com.yangdai.calc.compass;
/*           License Statement from the NOAA
The WMM source code is in the public domain and not licensed or 
under copyright. The information and software may be used freely 
by the public. As required by 17 U.S.C. 403, third parties producing 
copyrighted works consisting predominantly of the material produced 
by U.S. government agencies must provide notice with such work(s) 
identifying the U.S. Government material incorporated and stating 
that such material is not subject to copyright protection. 
 */
/* 美国国家海洋和大气管理局（NOAA）的许可声明
WMM源代码属于公有领域，无需许可或受版权保护。
公众可以自由使用这些信息和软件。
根据17 U.S.C. 403的规定，第三方如要制作的版权作品主要由美国政府机构制作的材料组成，
必须在该作品中提供通知，标明美国政府材料的使用并声明该材料不受版权保护。 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p>
 * 最后更新日期：2020年1月6日</p><p>
 * <b>注意：</b>如果您的应用程序不使用log4j，请注释掉日志记录器的引用，并恢复System.out.println语句。
 * 方法的输入没有进行有效范围的检查。</p><p>
 * <p>
 * 通过使用2020年时期更新附带的测试值进行JUnit测试验证。</p><p>
 * <p>
 * 这是一个用于生成地球上任意点的磁偏角、磁场强度和倾角的类。
 * 真实方位角 = 磁方位角 + 磁偏角。
 * 该类是根据NOAA国家数据中心的一个小程序修改而来，网址为
 * <a href="http://www.ngdc.noaa.gov/seg/segd.shtml">http://www.ngdc.noaa.gov/seg/segd.shtml</a>。
 * 所有计算没有改变。该类需要一个名为WMM.COF的输入文件，该文件必须与应用程序运行的目录中的文件相同。<br>
 * <b>注意：</b>如果WMM.COF文件丢失，将使用2020年的内部拟合系数。
 * <p>
 * 使用正确的日期，偏角精确到约0.5度。</p><p>
 * <p>
 * 这是LANL D-3版的GeoMagnetic计算器，源自NOAA国家数据中心的网址http://www.ngdc.noaa.gov/seg/segd.shtml。</p><p>
 * <p>
 * 由洛斯阿拉莫斯国家实验室的John St. Ledger改编于1999年6月25日</p><p>
 * <p>
 * <p>
 * 第2版备注：世界磁模型每5年更新一次。
 * 2000年的数据使用相同的算法计算磁场变量。唯一的改变在于输入文件中的球谐系数。
 * 输入文件的名称已更改为WMM.COF。日期也修正为2001年1月1日。
 * 此外，用于StreamTokenizer的已弃用的构造函数被替换，并且catch子句中的错误消息已更改。
 * 添加了获取磁场强度和倾角的方法。</p><p>
 * <p>
 * 发现了一些关于高度的有趣信息。计算中输入的高度是相对于WGS84椭球体的高度，而不是相对于MSL的高度。
 * 使用MSL高度意味着高度可能有多达200米的误差。对于我们的应用程序来说，这应该不是很重要。</p>
 *
 * <p><b>注意：</b>该类不是线程安全的。</p>
 *
 * @author 30415
 * @version 5.9 2020年1月6日
 * <p>将内部系数更新为2020年时期的值。通过新的JUnit测试。
 */
public class TSAGeoMag {
    /**
     * 输入字符串数组，包含wmm.cof输入文件的每一行输入。
     * 添加此数组是为了将所有数据内部化，使应用程序无需携带数据文件。
     * 在TSAGeoMag类中，此文件的列如下：
     * n, m, gnm, hnm, dgnm, dhnm
     */
    private final String[] input =
            {"   2020.0            WMM-2020        12/10/2019",
                    "  1  0  -29404.5       0.0        6.7        0.0",
                    "  1  1   -1450.7    4652.9        7.7      -25.1",
                    "  2  0   -2500.0       0.0      -11.5        0.0",
                    "  2  1    2982.0   -2991.6       -7.1      -30.2",
                    "  2  2    1676.8    -734.8       -2.2      -23.9",
                    "  3  0    1363.9       0.0        2.8        0.0",
                    "  3  1   -2381.0     -82.2       -6.2        5.7",
                    "  3  2    1236.2     241.8        3.4       -1.0",
                    "  3  3     525.7    -542.9      -12.2        1.1",
                    "  4  0     903.1       0.0       -1.1        0.0",
                    "  4  1     809.4     282.0       -1.6        0.2",
                    "  4  2      86.2    -158.4       -6.0        6.9",
                    "  4  3    -309.4     199.8        5.4        3.7",
                    "  4  4      47.9    -350.1       -5.5       -5.6",
                    "  5  0    -234.4       0.0       -0.3        0.0",
                    "  5  1     363.1      47.7        0.6        0.1",
                    "  5  2     187.8     208.4       -0.7        2.5",
                    "  5  3    -140.7    -121.3        0.1       -0.9",
                    "  5  4    -151.2      32.2        1.2        3.0",
                    "  5  5      13.7      99.1        1.0        0.5",
                    "  6  0      65.9       0.0       -0.6        0.0",
                    "  6  1      65.6     -19.1       -0.4        0.1",
                    "  6  2      73.0      25.0        0.5       -1.8",
                    "  6  3    -121.5      52.7        1.4       -1.4",
                    "  6  4     -36.2     -64.4       -1.4        0.9",
                    "  6  5      13.5       9.0       -0.0        0.1",
                    "  6  6     -64.7      68.1        0.8        1.0",
                    "  7  0      80.6       0.0       -0.1        0.0",
                    "  7  1     -76.8     -51.4       -0.3        0.5",
                    "  7  2      -8.3     -16.8       -0.1        0.6",
                    "  7  3      56.5       2.3        0.7       -0.7",
                    "  7  4      15.8      23.5        0.2       -0.2",
                    "  7  5       6.4      -2.2       -0.5       -1.2",
                    "  7  6      -7.2     -27.2       -0.8        0.2",
                    "  7  7       9.8      -1.9        1.0        0.3",
                    "  8  0      23.6       0.0       -0.1        0.0",
                    "  8  1       9.8       8.4        0.1       -0.3",
                    "  8  2     -17.5     -15.3       -0.1        0.7",
                    "  8  3      -0.4      12.8        0.5       -0.2",
                    "  8  4     -21.1     -11.8       -0.1        0.5",
                    "  8  5      15.3      14.9        0.4       -0.3",
                    "  8  6      13.7       3.6        0.5       -0.5",
                    "  8  7     -16.5      -6.9        0.0        0.4",
                    "  8  8      -0.3       2.8        0.4        0.1",
                    "  9  0       5.0       0.0       -0.1        0.0",
                    "  9  1       8.2     -23.3       -0.2       -0.3",
                    "  9  2       2.9      11.1       -0.0        0.2",
                    "  9  3      -1.4       9.8        0.4       -0.4",
                    "  9  4      -1.1      -5.1       -0.3        0.4",
                    "  9  5     -13.3      -6.2       -0.0        0.1",
                    "  9  6       1.1       7.8        0.3       -0.0",
                    "  9  7       8.9       0.4       -0.0       -0.2",
                    "  9  8      -9.3      -1.5       -0.0        0.5",
                    "  9  9     -11.9       9.7       -0.4        0.2",
                    " 10  0      -1.9       0.0        0.0        0.0",
                    " 10  1      -6.2       3.4       -0.0       -0.0",
                    " 10  2      -0.1      -0.2       -0.0        0.1",
                    " 10  3       1.7       3.5        0.2       -0.3",
                    " 10  4      -0.9       4.8       -0.1        0.1",
                    " 10  5       0.6      -8.6       -0.2       -0.2",
                    " 10  6      -0.9      -0.1       -0.0        0.1",
                    " 10  7       1.9      -4.2       -0.1       -0.0",
                    " 10  8       1.4      -3.4       -0.2       -0.1",
                    " 10  9      -2.4      -0.1       -0.1        0.2",
                    " 10 10      -3.9      -8.8       -0.0       -0.0",
                    " 11  0       3.0       0.0       -0.0        0.0",
                    " 11  1      -1.4      -0.0       -0.1       -0.0",
                    " 11  2      -2.5       2.6       -0.0        0.1",
                    " 11  3       2.4      -0.5        0.0        0.0",
                    " 11  4      -0.9      -0.4       -0.0        0.2",
                    " 11  5       0.3       0.6       -0.1       -0.0",
                    " 11  6      -0.7      -0.2        0.0        0.0",
                    " 11  7      -0.1      -1.7       -0.0        0.1",
                    " 11  8       1.4      -1.6       -0.1       -0.0",
                    " 11  9      -0.6      -3.0       -0.1       -0.1",
                    " 11 10       0.2      -2.0       -0.1        0.0",
                    " 11 11       3.1      -2.6       -0.1       -0.0",
                    " 12  0      -2.0       0.0        0.0        0.0",
                    " 12  1      -0.1      -1.2       -0.0       -0.0",
                    " 12  2       0.5       0.5       -0.0        0.0",
                    " 12  3       1.3       1.3        0.0       -0.1",
                    " 12  4      -1.2      -1.8       -0.0        0.1",
                    " 12  5       0.7       0.1       -0.0       -0.0",
                    " 12  6       0.3       0.7        0.0        0.0",
                    " 12  7       0.5      -0.1       -0.0       -0.0",
                    " 12  8      -0.2       0.6        0.0        0.1",
                    " 12  9      -0.5       0.2       -0.0       -0.0",
                    " 12 10       0.1      -0.9       -0.0       -0.0",
                    " 12 11      -1.1      -0.0       -0.0        0.0",
                    " 12 12      -0.3       0.5       -0.1       -0.1"
            };

    /**
     * 大地高度，单位为千米。是一个输入参数，在该类中被设置为零。
     * 在版本5中改回了输入参数。如果未指定，则默认为0。
     */
    private double alt = 0;

    /**
     * 大地纬度，单位为度。是一个输入参数。
     */
    private double glat = 0;

    /**
     * 大地经度，单位为度。是一个输入参数。
     */
    private double glon = 0;

    /**
     * 时间，以十进制年为单位。是一个输入参数。
     */
    private double time = 0;

    /**
     * 地磁偏角，单位为度。
     * 东方为正，西方为负。
     * （变化的负值）
     */
    private double dec = 0;

    /**
     * 地磁倾角，单位为度。
     * 向下为正，向上为负。
     */
    private double dip = 0;

    /**
     * 地磁总强度，以纳特为单位。
     */
    private double ti = 0;

    /*
    地磁网格偏差，相对于网格北方向。
    在版本5.0中不计算或输出。
    */

    /**
     * 球谐模型的最大度数。
     */
    private final int maxdeg = 12;

    /**
     * 球谐模型的最大阶数。
     */
    private int maxord;

    /**
     * 在版本5中添加。在早期版本中，计算的日期是作为常量保存的。
     * 现在默认日期设置为读取的时代加上2.5年。
     */
    private double defaultDate = 2022.5;

    /**
     * 在版本5中添加。在早期版本中，计算的高度被固定为0。
     * 在版本5中，如果计算中未指定高度，则默认使用此高度。
     */
    private final double defaultAltitude = 0;

    /**
     * 主要地磁模型的高斯系数（nt）。
     */
    private final double[][] c = new double[13][13];

    /**
     * 年变地磁模型的高斯系数（nt/yr）。
     */
    private final double[][] cd = new double[13][13];

    /**
     * 调整时间的地磁高斯系数（nt）。
     */
    private final double[][] tc = new double[13][13];

    /**
     * p(n,m)的θ导数（未归一化）。
     */
    private final double[][] dp = new double[13][13];

    /**
     * Schmidt归一化因子。
     */
    private final double[] snorm = new double[169];

    /**
     * 正弦（m*球谐坐标经度）。
     */
    private final double[] sp = new double[13];

    /**
     * 余弦（m*球谐坐标经度）。
     */
    private final double[] cp = new double[13];

    private final double[] fn = new double[13];
    private final double[] fm = new double[13];

    /**
     * m=1时的相关勒让德多项式（未归一化）。
     */
    private final double[] pp = new double[13];

    private final double[][] k = new double[13][13];

    /**
     * otime（旧时间），oalt（旧海拔），olat（旧纬度），olon（旧经度）变量用于
     * 存储上一次计算中使用的值，以节省计算时间（如果某些输入未更改）。
     */
    private double otime, oalt, olat, olon;

    /**
     * 基于拟合系数的有效时间起始日期（以年为单位）。
     */
    private double epoch;

    /**
     * bx是南北方向的磁场强度
     * by是东西方向的磁场强度
     * bz是垂直方向的磁场强度，向下为正
     * bh是水平方向的磁场强度
     */
    private double bx, by, bz, bh;

    /**
     * re是IAU-66椭球的平均半径，单位为km。
     * a2是WGS-84椭球的半长轴，单位为km，的平方。
     * b2是WGS-84椭球的半短轴，单位为km，的平方。
     * c2是c2 = a2 - b2
     * a4是a2的平方。
     * b4是b2的平方。
     * c4是c4 = a4 - b4。
     */
    private double re, a2, b2, c2, a4, b4, c4;

    private double r, d, ca, sa, ct, st;
    // 即使这些值只在一个方法中出现，也必须在此处创建，
    // 否则将无法计算出正确的值
    // 仅当海拔高度发生变化时，这些值才会被重新计算。


    /**
     * 通过调用initModel()方法实例化对象。
     */
    public TSAGeoMag() {
        // 从文件中读取模型数据并初始化GeoMag计算
        initModel();
    }

    /**
     * 从文件中读取数据并初始化地磁模型。如果
     * 文件不存在或发生IO异常，则将使用内部的
     * 2015年有效的值。请注意，WMM.COF文件的最后一行必须为9999...，
     * 才能正确读取输入文件。
     */
    private void initModel() {
        glat = 0;
        glon = 0;
        // 初始化常量
        maxord = maxdeg;
        sp[0] = 0.0;
        cp[0] = snorm[0] = pp[0] = 1.0;
        dp[0][0] = 0.0;
        /*
          WGS-84椭球的半长轴，单位为km。
         */
        double a = 6378.137;
        /*
          WGS-84椭球的半短轴，单位为km。
         */
        double b = 6356.7523142;
         /*
         IAU-66椭球的平均半径，单位为km。
        */
        re = 6371.2;
        a2 = a * a;
        b2 = b * b;
        c2 = a2 - b2;
        a4 = a2 * a2;
        b4 = b2 * b2;
        c4 = a4 - b4;

        try {
            // 打开数据文件并解析数值
            //InputStream is;
            Reader is;

            InputStream input = getClass().getResourceAsStream("WMM.COF");
            if (input == null) {
                throw new FileNotFoundException("WMM.COF文件未找到");
            }
            is = new InputStreamReader(input);
            StreamTokenizer str = new StreamTokenizer(is);


            // 读取世界磁场模型球谐系数
            c[0][0] = 0.0;
            cd[0][0] = 0.0;
            str.nextToken();
            epoch = str.nval;
            defaultDate = epoch + 2.5;

            str.nextToken();
            str.nextToken();

            // 循环从文件中获取数据
            while (true) {
                str.nextToken();
                // 文件结束
                if (str.nval >= 9999) {
                    break;
                }

                int n = (int) str.nval;
                str.nextToken();
                int m = (int) str.nval;
                str.nextToken();
                double gnm = str.nval;
                str.nextToken();
                double hnm = str.nval;
                str.nextToken();
                double dgnm = str.nval;
                str.nextToken();
                double dhnm = str.nval;

                if (m <= n) {
                    c[m][n] = gnm;
                    cd[m][n] = dgnm;

                    if (m != 0) {
                        c[n][m - 1] = hnm;
                        cd[n][m - 1] = dhnm;
                    }
                }

            }

            is.close();
        }
        // 版本2，单独捕获FileNotFound和IO异常，而不是捕获所有异常。
        // 版本5.4添加日志记录支持，并注释掉System.out.println
        catch (
                IOException e) {
            setCoeff();
        }
        // 将Schmidt标准化的高斯系数转换为非标准化形式
        snorm[0] = 1.0;
        for (
                int n = 1;
                n <= maxord; n++) {

            snorm[n] = snorm[n - 1] * (2 * n - 1) / n;
            int j = 2;

            for (int m = 0, d1 = 1, d2 = (n - m + d1) / d1; d2 > 0; d2--, m += d1) {
                k[m][n] = (double) (((n - 1) * (n - 1)) - (m * m)) / (double) ((2 * n - 1) * (2 * n - 3));
                if (m > 0) {
                    double flnmj = ((n - m + 1) * j) / (double) (n + m);
                    snorm[n + m * 13] = snorm[n + (m - 1) * 13] * Math.sqrt(flnmj);
                    j = 1;
                    c[n][m - 1] = snorm[n + m * 13] * c[n][m - 1];
                    cd[n][m - 1] = snorm[n + m * 13] * cd[n][m - 1];
                }
                c[m][n] = snorm[n + m * 13] * c[m][n];
                cd[m][n] = snorm[n + m * 13] * cd[m][n];
            }

            fn[n] = (n + 1);
            fm[n] = n;

        }

        k[1][1] = 0.0;

        otime = oalt = olat = olon = -1000.0;
    }

    /**
     * <p><b>目的：</b>此程序计算地球磁场在大地坐标系下的偏角（DEC）、倾角（DIP）、总强度（TI）和网格偏差（GV - 仅极地地区，参考极地立体投影的网格北方向）。
     * 该计算基于当前官方的美国国防部（DOD）球谐系数世界磁场模型（WMM-2010）。WMM系列模型每5年更新一次，更新日期为可被5整除的年份（例如1980年、1985年、1990年等）。
     * 更新由美国海洋测绘办公室与英国地质调查局（BGS）合作进行。该模型基于来自飞机、卫星和地磁观测站的地磁测量数据。</p><p>
     *
     *
     *
     * <b>精度：</b>在地球表面的海洋区域，对于像WMM-95这样的5年生命周期的12阶球谐模型，各磁性分量的估计均方根误差如下：</p>
     * <ul>
     *                DEC  -   0.5度<br>
     *                DIP  -   0.5度<br>
     *                TI   - 280.0纳特斯拉（nT）<br>
     *                GV   -   0.5度<br></ul>
     *
     *                <p>从这四个分量可以通过简单的三角关系推导得到其他磁性分量，这些分量在海洋区域的近似误差如下：</p>
     * <ul>
     *                X    - 140 nT（北向）<br>
     *                Y    - 140 nT（东向）<br>
     *                Z    - 200 nT（竖直向下为正）<br>
     *                H    - 200 nT（水平向）<br></ul>
     *
     *                <p>在陆地上，均方根误差预计会稍高，尽管DEC、DIP和GV的均方根误差在整个5年模型生命周期内仍估计小于0.5度。
     *                其他陆地上的分量误差更难估计，因此没有给出。</p><p>
     * <p>
     *                所有四个地磁参数在任何给定时间的精度取决于地磁纬度。误差在赤道最小，在磁极最大。</p><p>
     * <p>
     *                非常重要的是要注意，12阶模型（如WMM-2010）仅描述由地球核心引起的长波长空间磁场波动。
     *                WMM系列模型不包
     * <p>
     * 括地磁场在地幔和地壳中起源的中、短波长空间波动。
     *                因此，在地表各个位置（主要是陆地、大陆边缘和海洋海山、海岭和海沟）上，可能会出现几度的隔离角度误差。
     *                模型还不包括地球磁场由磁气层和电离层引起的非世俗时间波动。
     *                在磁暴期间，时间波动可能导致地磁场与模型值的显著偏差。
     *                在北极和南极地区以及赤道地区，偏差从模型值是频繁和持久的。</p><p>
     * <p>
     *                如果所需的DECLINATION精度比WMM系列模型提供的更严格，则建议用户请求进行特殊（区域或本地）测量，并由美国地质调查局（USGS）制作模型。
     *                此类请求应通过上述地址向NIMA提出。</p><p>
     * <p>
     * <p>
     * <p>
     *     注意：此版本的GEOMAG使用参考为WGS-84重力模型椭球体的WMM-2010地磁模型</p>
     *
     * @param fLat     纬度，以十进制度为单位。
     * @param fLon     经度，以十进制度为单位。
     * @param year     日期，以十进制年为单位。
     * @param altitude 海拔高度，以千米为单位。
     */
    private void calcGeoMag(double fLat, double fLon, double year, double altitude) {

        glat = fLat;
        glon = fLon;
        alt = altitude;

        time = year;

        double dt = time - epoch;

        double pi = Math.PI;
        double dtr = (pi / 180.0);
        double rlon = glon * dtr;
        double rlat = glat * dtr;
        double srlon = Math.sin(rlon);
        double srlat = Math.sin(rlat);
        double crlon = Math.cos(rlon);
        double crlat = Math.cos(rlat);
        double srlat2 = srlat * srlat;
        double crlat2 = crlat * crlat;
        sp[1] = srlon;
        cp[1] = crlon;

        // 转换从大地坐标系到球坐标系
        if (alt != oalt || glat != olat) {
            double q = Math.sqrt(a2 - c2 * srlat2);
            double q1 = alt * q;
            double q2 = ((q1 + a2) / (q1 + b2)) * ((q1 + a2) / (q1 + b2));
            ct = srlat / Math.sqrt(q2 * crlat2 + srlat2);
            st = Math.sqrt(1.0 - (ct * ct));
            double r2 = ((alt * alt) + 2.0 * q1 + (a4 - c4 * srlat2) / (q * q));
            r = Math.sqrt(r2);
            d = Math.sqrt(a2 * crlat2 + b2 * srlat2);
            ca = (alt + d) / r;
            sa = c2 * crlat * srlat / (r * d);
        }
        if (glon != olon) {
            for (int m = 2; m <= maxord; m++) {
                sp[m] = sp[1] * cp[m - 1] + cp[1] * sp[m - 1];
                cp[m] = cp[1] * cp[m - 1] - sp[1] * sp[m - 1];
            }
        }
        double aor = re / r;
        double ar = aor * aor;
        double br = 0, bt = 0, bp = 0, bpp = 0;

        for (int n = 1; n <= maxord; n++) {
            ar = ar * aor;
            for (int m = 0, d3 = 1, d4 = (n + m + d3) / d3; d4 > 0; d4--, m += d3) {

                // 计算未标准化的关联Legendre多项式和导数（通过递归关系）
                if (alt != oalt || glat != olat) {
                    if (n == m) {
                        snorm[n + m * 13] = st * snorm[n - 1 + (m - 1) * 13];
                        dp[m][n] = st * dp[m - 1][n - 1] + ct * snorm[n - 1 + (m - 1) * 13];
                    }
                    if (n == 1 && m == 0) {
                        snorm[n + m * 13] = ct * snorm[n - 1 + m * 13];
                        dp[m][n] = ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13];
                    }
                    if (n > 1 && n != m) {
                        if (m > n - 2) {
                            snorm[n - 2 + m * 13] = 0.0;
                        }
                        if (m > n - 2) {
                            dp[m][n - 2] = 0.0;
                        }
                        snorm[n + m * 13] = ct * snorm[n - 1 + m * 13] - k[m][n] * snorm[n - 2 + m * 13];
                        dp[m][n] = ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13] - k[m][n] * dp[m][n - 2];
                    }
                }

                // 时间调整高斯系数
                if (time != otime) {
                    tc[m][n] = c[m][n] + dt * cd[m][n];

                    if (m != 0) {
                        tc[n][m - 1] = c[n][m - 1] + dt * cd[n][m - 1];
                    }
                }

                // 累积球谐展开的项
                double temp1, temp2;
                double par = ar * snorm[n + m * 13];
                if (m == 0) {
                    temp1 = tc[m][n] * cp[m];
                    temp2 = tc[m][n] * sp[m];
                } else {
                    temp1 = tc[m][n] * cp[m] + tc[n][m - 1] * sp[m];
                    temp2 = tc[m][n] * sp[m] - tc[n][m - 1] * cp[m];
                }

                bt = bt - ar * temp1 * dp[m][n];
                bp += (fm[m] * temp2 * par);
                br += (fn[n] * temp1 * par);

                // 特殊情况：北/南地理极点
                if (st == 0.0 && m == 1) {
                    if (n == 1) {
                        pp[n] = pp[n - 1];
                    } else {
                        pp[n] = ct * pp[n - 1] - k[m][n] * pp[n - 2];
                    }
                    double parp = ar * pp[n];
                    bpp += (fm[m] * temp2 * parp);
                }

            }    //for(m...)

        }    //for(n...)


        if (st == 0.0) {
            bp = bpp;
        } else {
            bp /= st;
        }

        // 将磁向量分量从球坐标系旋转到大地坐标系
// by 是东西方向的磁场分量
// bx 是南北方向的磁场分量
// bz 是垂直方向的磁场分量。
        bx = -bt * ca - br * sa;
        by = bp;
        bz = bt * sa - br * ca;

// 计算偏角（DEC），倾角（DIP）和总强度（TI）

        bh = Math.sqrt((bx * bx) + (by * by));
        ti = Math.sqrt((bh * bh) + (bz * bz));
// 计算偏角。
        dec = (Math.atan2(by, bx) / dtr);
        dip = (Math.atan2(bz, bh) / dtr);

// 这是用于网格导航的变化。
// 目前未使用。参见St. Ledger的解释。
// 如果当前的大地位置位于北极或南极地区
//（即GLAT> + 55度或GLAT < -55度），计算磁网偏差
// 网格北极是参考极投影的0子午线。

// 否则，将磁网偏差设置为-999.0

        otime = time;
        oalt = alt;
        olat = glat;
        olon = glon;
    }

    /**
     * 返回美国国防部地磁模型和数据中的磁偏角（declination），单位为度。磁航向 + 磁偏角 = 真航向。默认情况下，日期和海拔高度分别为有效5年期间的中间日期和0海拔高度。
     * （真航向 + 磁变 = 磁航向。）
     *
     * @param dlong 十进制度数的经度。
     * @param dlat  十进制度数的纬度。
     * @return 磁偏角，单位为度。
     */
    public double getDeclination(double dlat, double dlong) {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
        return dec;
    }

    /**
     * 返回美国国防部地磁模型和数据中的磁偏角（declination），单位为度。
     * 磁航向 + 磁偏角 = 真航向。
     * （真航向 + 磁变 = 磁航向。）
     *
     * @param dlong    十进制度数的经度。
     * @param dlat     十进制度数的纬度。
     * @param year     十进制年份的日期。
     * @param altitude 海拔高度，以公里为单位。
     * @return 磁偏角，单位为度。
     */
    public double getDeclination(double dlat, double dlong, double year, double altitude) {
        calcGeoMag(dlat, dlong, year, altitude);
        return dec;
    }

    /**
     * 返回美国国防部地磁模型和数据中的磁场强度（intensity），以纳特为单位。默认情况下，日期和海拔高度分别为有效5年期间的中间日期和0海拔高度。
     *
     * @param dlong 十进制度数的经度。
     * @param dlat  十进制度数的纬度。
     * @return 磁场强度，以纳特为单位。
     */
    public double getIntensity(double dlat, double dlong) {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
        return ti;
    }

    /**
     * 返回美国国防部地磁模型和数据中的磁场强度（intensity），以纳特为单位。
     *
     * @param dlong    十进制度数的经度。
     * @param dlat     十进制度数的纬度。
     * @param year     十进制年份的日期。
     * @param altitude 海拔高度，以公里为单位。
     * @return 磁场强度，以纳特为单位。
     */
    public double getIntensity(double dlat, double dlong, double year, double altitude) {
        calcGeoMag(dlat, dlong, year, altitude);
        return ti;
    }

    /**
     * 返回美国国防部地磁模型和数据中的水平磁场强度（horizontal intensity），以纳特为单位。默认情况下，日期和海拔高度分别为有效5年期间的中间日期和0海拔高度。
     *
     * @param dlong 十进制度数的经度。
     * @param dlat  十进制度数的纬度。
     * @return 水平磁场强度，以纳特为单位。
     */
    public double getHorizontalIntensity(double dlat, double dlong) {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
        return bh;
    }

    /**
     * 返回美国国防部地磁模型和数据中的水平磁场强度（horizontal intensity），以纳特为单位。
     *
     * @param dlong    十进制度数的经度。
     * @param dlat     十进制度数的纬度。
     * @param year     十进制年份的日期。
     * @param altitude 海拔高度，以公里为单位。
     * @return 水平磁场强度，以纳特为单位。
     */
    public double getHorizontalIntensity(double dlat, double dlong, double year, double altitude) {
        calcGeoMag(dlat, dlong, year, altitude);
        return bh;
    }


    /**
     * 返回美国国防部地磁模型和数据中的垂直磁场强度（vertical intensity），以纳特为单位。默认情况下，日期和海拔高度分别为有效5年期间的中间日期和0海拔高度。
     *
     * @param dlong 十进制度数的经度。
     * @param dlat  十进制度数的纬度。
     * @return 垂直磁场强度，以纳特为单位。
     */
    public double getVerticalIntensity(double dlat, double dlong) {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
        return bz;
    }

    /**
     * 返回美国国防部地磁模型和数据中的垂直磁场强度（vertical intensity），以纳特为单位。
     *
     * @param dlong    十进制度数的经度。
     * @param dlat     十进制度数的纬度。
     * @param year     十进制年份的日期。
     * @param altitude 海拔高度，以公里为单位。
     * @return 垂直磁场强度，以纳特为单位。
     */
    public double getVerticalIntensity(double dlat, double dlong, double year, double altitude) {
        calcGeoMag(dlat, dlong, year, altitude);
        return bz;
    }

    /**
     * 返回美国国防部地磁模型和数据中的北向磁场强度（northerly intensity），以纳特为单位。默认情况下，日期和海拔高度分别为有效5年期间的中间日期和0海拔高度。
     *
     * @param dlong 十进制度数的经度。
     * @param dlat  十进制度数的纬度。
     * @return 磁场强度的北向分量，以纳特为单位。
     */
    public double getNorthIntensity(double dlat, double dlong) {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
        return bx;
    }

    /**
     * 返回美国国防部地磁模型和数据中的北向磁场强度（northerly intensity），以纳特为单位。
     *
     * @param dlong    十进制度数的经度。
     * @param dlat     十进制度数的纬度。
     * @param year     十进制年份的日期。
     * @param altitude 海拔高度，以公里为单位。
     * @return 磁场强度的北向分量，以纳特为单位。
     */
    public double getNorthIntensity(double dlat, double dlong, double year, double altitude) {
        calcGeoMag(dlat, dlong, year, altitude);
        return bx;
    }

    /**
     * 返回美国国防部地磁模型和数据中的东向磁场强度（easterly intensity），以纳特为单位。默认情况下，日期和海拔高度分别为有效5年期间的中间日期和0海拔高度。
     *
     * @param dlong 十进制度数的经度。
     * @param dlat  十进制度数的纬度。
     * @return 磁场强度的东向分量，以纳特为单位。
     */
    public double getEastIntensity(double dlat, double dlong) {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
        return by;
    }

    /**
     * 返回美国国防部地磁模型和数据中的东向磁场强度（easterly intensity），以纳特为单位。
     *
     * @param dlong    十进制度数的经度。
     * @param dlat     十进制度数的纬度。
     * @param year     十进制年份的日期。
     * @param altitude 海拔高度，以公里为单位。
     * @return 磁场强度的东向分量，以纳特为单位。
     */
    public double getEastIntensity(double dlat, double dlong, double year, double altitude) {
        calcGeoMag(dlat, dlong, year, altitude);
        return by;
    }


    /**
     * 返回美国国防部地磁模型和数据中的磁场倾角（magnetic field dip angle），以度为单位。默认情况下，日期和海拔高度分别为有效5年期间的中间日期和0海拔高度。
     *
     * @param dlong 十进制度数的经度。
     * @param dlat  十进制度数的纬度。
     * @return 磁场倾角，以度为单位。
     */
    public double getDipAngle(double dlat, double dlong) {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
        return dip;
    }

    /**
     * 返回美国国防部地磁模型和数据中的磁场倾角（magnetic field dip angle），以度为单位。
     *
     * @param dlong    十进制度数的经度。
     * @param dlat     十进制度数的纬度。
     * @param year     十进制年份的日期。
     * @param altitude 海拔高度，以公里为单位。
     * @return 磁场倾角，以度为单位。
     */
    public double getDipAngle(double dlat, double dlong, double year, double altitude) {
        calcGeoMag(dlat, dlong, year, altitude);
        return dip;
    }

    /**
     * 将输入数据设置为内部拟合系数。
     * 如果读取输入文件 WMM.COF 时出现异常，将使用这些值。
     * <p>
     * 注意：除非 WMM.COF 文件缺失，否则此方法不会被 JUnit 测试检查。
     */
    private void setCoeff() {
        c[0][0] = 0.0;
        cd[0][0] = 0.0;

        epoch = Double.parseDouble(input[0].trim().split("\\s+")[0]);
        defaultDate = epoch + 2.5;

        String[] tokens;

        // 从内部值中获取数据的循环
        for (int i = 1; i < input.length; i++) {
            tokens = input[i].trim().split("\\s+");

            int n = Integer.parseInt(tokens[0]);
            int m = Integer.parseInt(tokens[1]);
            double gnm = Double.parseDouble(tokens[2]);
            double hnm = Double.parseDouble(tokens[3]);
            double dgnm = Double.parseDouble(tokens[4]);
            double dhnm = Double.parseDouble(tokens[5]);

            if (m <= n) {
                c[m][n] = gnm;
                cd[m][n] = dgnm;

                if (m != 0) {
                    c[n][m - 1] = hnm;
                    cd[n][m - 1] = dhnm;
                }
            }
        }
    }

    /**
     * <p>
     * 给定一个公历日期对象，返回该日期对应的十进制年份值，精确到日期的天数。忽略小时、分钟和秒。</p><p>
     * <p>
     * 如果输入的公历日期为 new GregorianCalendar(2012, 6, 1)，则计算全年七月份，返回2012.5（366天中的183天）。</p><p>
     * <p>
     * 如果输入的公历日期为 new GregorianCalendar(2010, 0, 0)，则不计算一月一日，返回2010.0</p><p>
     *
     * @param cal 日期（年、月和日）
     * @return 十进制年份的日期
     */
    public double decimalYear(GregorianCalendar cal) {
        int year = cal.get(Calendar.YEAR);
        double daysInYear;
        if (cal.isLeapYear(year)) {
            daysInYear = 366.0;
        } else {
            daysInYear = 365.0;
        }

        return year + (cal.get(Calendar.DAY_OF_YEAR)) / daysInYear;
    }
}

