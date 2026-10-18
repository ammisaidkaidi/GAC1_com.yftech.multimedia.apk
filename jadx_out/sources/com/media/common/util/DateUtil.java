package com.media.common.util;

import android.text.format.DateUtils;
import com.media.common.ui.widget.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public class DateUtil {
    public static String dateTransform(String str, String str2) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z", Locale.getDefault()).parse(str.replace("Z", " UTC"));
            return date != null ? new SimpleDateFormat(str2, Locale.getDefault()).format(date) : "";
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String dateTransform(String str, String str2, String str3) {
        try {
            Date date = new SimpleDateFormat(str2, Locale.getDefault()).parse(str);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str3, Locale.getDefault());
            if (date != null) {
                return simpleDateFormat.format(date);
            }
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate(long j, String str) {
        return new SimpleDateFormat(str, Locale.getDefault()).format(new Date(j));
    }

    public static String formatDate(String str, String str2) {
        try {
            return new SimpleDateFormat("yyyy" + str2 + "MM" + str2 + "dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDate(Date date, String str) {
        return new SimpleDateFormat(str, Locale.getDefault()).format(date);
    }

    public static String formatDateMMDDHHMMSS(long j) {
        return new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date(j));
    }

    public static String formatDateMd(String str, String str2) {
        try {
            return new SimpleDateFormat("MM" + str2 + "dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long formatDateTime(String str, String str2) {
        try {
            return new SimpleDateFormat(str2, Locale.getDefault()).parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String formatDateYYYYMMDD(long j) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(j));
    }

    public static String formatDateYYYYMMDDHHMMSS(long j) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(j));
    }

    public static String formatDateYYYYMMDDHHMMSSDote(long j) {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date(j));
    }

    public static String formatHHMMSS(long j) {
        return new SimpleDateFormat("HH时mm分ss秒").format(new Date(j));
    }

    public static int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(11);
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static long getDayFromCurrentTime(String str) {
        return getTimeGapFromCurrentDate(str) / 86400000;
    }

    public static String getHourAndMin(long j) {
        return new SimpleDateFormat("HH:mm").format(new Date(j));
    }

    public static String getMusicTime(long j) {
        long j2 = j / 1000;
        long j3 = j2 / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", Long.valueOf(j3), Long.valueOf(j2 - (60 * j3)));
    }

    public static String getMusicTime(long j, boolean z) {
        if (!z) {
            return getMusicTime(j);
        }
        long j2 = j / 1000;
        long j3 = j2 / 60;
        long j4 = j3 / 60;
        if (j4 <= 0) {
            return String.format(Locale.getDefault(), "%02d:%02d", Long.valueOf(j3), Long.valueOf(j2 - (60 * j3)));
        }
        long j5 = j3 - (j4 * 60);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", Long.valueOf(j4), Long.valueOf(j5), Long.valueOf(j2 - (60 * j5)));
    }

    public static String getNewChatTime(String str) {
        try {
            String[] strArr = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str).getTime();
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(time);
            String str2 = "";
            int i = calendar2.get(11);
            if (i >= 0 && i < 6) {
                str2 = "凌晨";
            } else if (i >= 6 && i < 12) {
                str2 = "早上";
            } else if (i == 12) {
                str2 = "中午";
            } else if (i > 12 && i < 18) {
                str2 = "下午";
            } else if (i >= 18) {
                str2 = "晚上";
            }
            String str3 = "M月d日 " + str2 + "HH:mm";
            String str4 = "yyyy年M月d日 " + str2 + "HH:mm";
            if (!(calendar.get(1) == calendar2.get(1))) {
                return getYearTime(time, str4);
            }
            if (calendar.get(2) != calendar2.get(2)) {
                return getTime(time, str3);
            }
            switch (calendar.get(5) - calendar2.get(5)) {
                case 0:
                    return getHourAndMin(time);
                case 1:
                    return "昨天 " + getHourAndMin(time);
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    if (calendar2.get(4) == calendar.get(4) && calendar2.get(7) != 1) {
                        return strArr[calendar2.get(7) - 1] + getHourAndMin(time);
                    }
                    return getTime(time, str3);
                default:
                    return getTime(time, str3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getSimpleTime(long j) {
        try {
            Date date = new Date();
            Date date2 = new Date(j);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date2);
            int i = calendar.get(1);
            int i2 = calendar.get(2);
            int i3 = calendar.get(3);
            int i4 = calendar.get(6);
            int i5 = calendar.get(7);
            calendar.setTime(date);
            int i6 = calendar.get(1);
            int i7 = calendar.get(2);
            int i8 = calendar.get(6);
            int i9 = calendar.get(3);
            LogUtil.d("get the weekIndex:" + i3 + ", weekNow:" + i9);
            if (i6 != i) {
                return new SimpleDateFormat(BaseApplication.getInstance().getApplicationContext().getString(R.string.date_year_month_day), Locale.getDefault()).format(date2);
            }
            if (i2 == i7 && i4 == i8) {
                return BaseApplication.getInstance().getApplicationContext().getString(R.string.date_today);
            }
            if (i4 + 1 == i8) {
                return BaseApplication.getInstance().getApplicationContext().getString(R.string.date_yesterday);
            }
            if (i4 + 2 == i8) {
                return BaseApplication.getInstance().getApplicationContext().getString(R.string.date_the_day_before_yesterday);
            }
            return (i3 != i9 || i4 + 3 > i8 || i4 + 6 < i8) ? new SimpleDateFormat(BaseApplication.getInstance().getApplicationContext().getString(R.string.date_month_day), Locale.getDefault()).format(date2) : BaseApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.date_week)[i5 - 1];
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTime(long j) {
        long j2 = j / 1000;
        long j3 = j2 / 60;
        long j4 = j3 / 60;
        long j5 = j4 * 60;
        long j6 = j3 - j5;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", Long.valueOf(j4), Long.valueOf(j6), Long.valueOf((j2 - (j6 * 60)) - (j5 * 60)));
    }

    public static String getTime(long j, String str) {
        return new SimpleDateFormat(str).format(new Date(j));
    }

    public static String getTimeDifferenceNow(long j) {
        try {
            long jCurrentTimeMillis = (System.currentTimeMillis() - j) / 1000;
            long j2 = jCurrentTimeMillis / 60;
            long j3 = j2 / 60;
            long j4 = j3 / 24;
            long j5 = j4 / 30;
            long j6 = j5 / 12;
            if (j6 != 0) {
                return j6 + "年前";
            }
            if (j5 != 0 && j5 < 12) {
                return j5 + "月前";
            }
            if (j4 != 0 && j4 < 30) {
                return j4 + "天前";
            }
            if (j3 != 0 && j3 < 24) {
                return j3 + "小时前";
            }
            if (j2 != 0 && j2 < 60) {
                return j2 + "分钟前";
            }
            if (jCurrentTimeMillis <= 0 || jCurrentTimeMillis >= 60) {
                return "刚刚";
            }
            return jCurrentTimeMillis + "秒前";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTimeDifferenceNow(String str) {
        try {
            long jCurrentTimeMillis = (System.currentTimeMillis() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str).getTime()) / 1000;
            long j = jCurrentTimeMillis / 60;
            long j2 = j / 60;
            long j3 = j2 / 24;
            long j4 = j3 / 30;
            long j5 = j4 / 12;
            if (j5 != 0) {
                return j5 + "年前";
            }
            if (j4 != 0 && j4 < 12) {
                return j4 + "月前";
            }
            if (j3 != 0 && j3 < 30) {
                return j3 + "天前";
            }
            if (j2 != 0 && j2 < 24) {
                return j2 + "小时前";
            }
            if (j != 0 && j < 60) {
                return j + "分钟前";
            }
            if (jCurrentTimeMillis == 0) {
                return "刚刚";
            }
            return jCurrentTimeMillis + "秒前";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTimeDifferenceNowForCollect(String str) {
        long j = Long.parseLong(str);
        Integer num = 1000;
        Integer numValueOf = Integer.valueOf(num.intValue() * 60);
        Integer numValueOf2 = Integer.valueOf(numValueOf.intValue() * 60);
        Integer numValueOf3 = Integer.valueOf(numValueOf2.intValue() * 24);
        Long lValueOf = Long.valueOf(j / ((long) numValueOf3.intValue()));
        Long lValueOf2 = Long.valueOf((j - (lValueOf.longValue() * ((long) numValueOf3.intValue()))) / ((long) numValueOf2.intValue()));
        Long lValueOf3 = Long.valueOf(((j - (lValueOf.longValue() * ((long) numValueOf3.intValue()))) - (lValueOf2.longValue() * ((long) numValueOf2.intValue()))) / ((long) numValueOf.intValue()));
        Long lValueOf4 = Long.valueOf((((j - (lValueOf.longValue() * ((long) numValueOf3.intValue()))) - (lValueOf2.longValue() * ((long) numValueOf2.intValue()))) - (lValueOf3.longValue() * ((long) numValueOf.intValue()))) / ((long) num.intValue()));
        StringBuffer stringBuffer = new StringBuffer();
        if (lValueOf2.longValue() > 0) {
            if (lValueOf.longValue() > 0) {
                stringBuffer.append(((lValueOf.longValue() * 24) + lValueOf2.longValue()) + "小时");
            } else {
                stringBuffer.append(lValueOf2 + "小时");
            }
        }
        if (lValueOf3.longValue() > 0) {
            stringBuffer.append(lValueOf3 + "分");
        }
        if (lValueOf4.longValue() > 0) {
            stringBuffer.append(lValueOf4 + "秒");
        }
        return stringBuffer.toString();
    }

    public static long getTimeForDate(long j) {
        LogUtil.d("getTimeForDate:Before=" + j);
        Date date = new Date(j);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(1), calendar.get(2), calendar.get(5), 0, 0, 0);
        long timeInMillis = calendar.getTimeInMillis();
        LogUtil.d("getTimeForDate:After=" + timeInMillis);
        return timeInMillis;
    }

    public static long getTimeForDateExtra(long j) {
        LogUtil.d("getTimeForDate:Before=" + j);
        long j2 = (j / 86400000) * 86400000;
        LogUtil.d("getTimeForDate:After=" + j2);
        return j2;
    }

    public static long getTimeGapFromCurrentDate(String str) {
        return Long.parseLong(str) - System.currentTimeMillis();
    }

    public static long getTimeGapFromCurrentDate(Date date) {
        return System.currentTimeMillis() - date.getTime();
    }

    public static String getYearTime(long j, String str) {
        return new SimpleDateFormat(str).format(new Date(j));
    }

    public static boolean overTime(String str, String str2, int i) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(str2);
            Date date2 = simpleDateFormat.parse(str);
            getCurrentTime();
            return (date.getTime() - date2.getTime()) / DateUtils.MINUTE_IN_MILLIS > ((long) i);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long passedMinute() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(11) * 60) + calendar.get(12);
    }
}
