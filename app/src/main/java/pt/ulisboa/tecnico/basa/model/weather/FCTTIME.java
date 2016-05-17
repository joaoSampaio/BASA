package pt.ulisboa.tecnico.basa.model.weather;

/**
 * Created by Sampaio on 26/04/2016.
 */
public class FCTTIME {

    private int hour;
    private String hour_padded;
    private String min;
    private String min_unpadded;
    private String sec;
    private int year;
    private int mon;
    private String mon_padded;
    private String mon_abbrev;
    private int mday;
    private String mday_padded;
    private String yday;
    private String isdst;
    private long epoch;
    private String pretty;
    private String civil;
    private String month_name;
    private String month_name_abbrev;
    private String weekday_name;
    private String weekday_name_night;
    private String weekday_name_abbrev;
    private String weekday_name_unlang;
    private String weekday_name_night_unlang;
    private String ampm;
    private String tz;
    private String age;
    private String UTCDATE;



//            "hour": "17",
//            "hour_padded": "17",
//            "min": "00",
//            "min_unpadded": "0",
//            "sec": "0",
//            "year": "2016",
//            "mon": "4",
//            "mon_padded": "04",
//            "mon_abbrev": "Apr",
//            "mday": "26",
//            "mday_padded": "26",
//            "yday": "116",
//            "isdst": "1",
//            "epoch": "1461686400",
//            "pretty": "05:00 PM WEST em 26 de Abril de 2016",
//            "civil": "5:00 PM",
//            "month_name": "Abril",
//            "month_name_abbrev": "Abr",
//            "weekday_name": "Terça-feira",
//            "weekday_name_night": "Terça à noite",
//            "weekday_name_abbrev": "Ter",
//            "weekday_name_unlang": "Tuesday",
//            "weekday_name_night_unlang": "Tuesday Night",
//            "ampm": "PM",
//            "tz": "",
//            "age": "",
//            "UTCDATE": ""


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getHour_padded() {
        return hour_padded;
    }

    public void setHour_padded(String hour_padded) {
        this.hour_padded = hour_padded;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMin_unpadded() {
        return min_unpadded;
    }

    public void setMin_unpadded(String min_unpadded) {
        this.min_unpadded = min_unpadded;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMon() {
        return mon;
    }

    public void setMon(int mon) {
        this.mon = mon;
    }

    public String getMon_padded() {
        return mon_padded;
    }

    public void setMon_padded(String mon_padded) {
        this.mon_padded = mon_padded;
    }

    public String getMon_abbrev() {
        return mon_abbrev;
    }

    public void setMon_abbrev(String mon_abbrev) {
        this.mon_abbrev = mon_abbrev;
    }

    public int getMday() {
        return mday;
    }

    public void setMday(int mday) {
        this.mday = mday;
    }

    public String getMday_padded() {
        return mday_padded;
    }

    public void setMday_padded(String mday_padded) {
        this.mday_padded = mday_padded;
    }

    public String getYday() {
        return yday;
    }

    public void setYday(String yday) {
        this.yday = yday;
    }

    public String getIsdst() {
        return isdst;
    }

    public void setIsdst(String isdst) {
        this.isdst = isdst;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public String getPretty() {
        return pretty;
    }

    public void setPretty(String pretty) {
        this.pretty = pretty;
    }

    public String getCivil() {
        return civil;
    }

    public void setCivil(String civil) {
        this.civil = civil;
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }

    public String getMonth_name_abbrev() {
        return month_name_abbrev;
    }

    public void setMonth_name_abbrev(String month_name_abbrev) {
        this.month_name_abbrev = month_name_abbrev;
    }

    public String getWeekday_name() {
        return weekday_name;
    }

    public void setWeekday_name(String weekday_name) {
        this.weekday_name = weekday_name;
    }

    public String getWeekday_name_night() {
        return weekday_name_night;
    }

    public void setWeekday_name_night(String weekday_name_night) {
        this.weekday_name_night = weekday_name_night;
    }

    public String getWeekday_name_abbrev() {
        return weekday_name_abbrev;
    }

    public void setWeekday_name_abbrev(String weekday_name_abbrev) {
        this.weekday_name_abbrev = weekday_name_abbrev;
    }

    public String getWeekday_name_unlang() {
        return weekday_name_unlang;
    }

    public void setWeekday_name_unlang(String weekday_name_unlang) {
        this.weekday_name_unlang = weekday_name_unlang;
    }

    public String getWeekday_name_night_unlang() {
        return weekday_name_night_unlang;
    }

    public void setWeekday_name_night_unlang(String weekday_name_night_unlang) {
        this.weekday_name_night_unlang = weekday_name_night_unlang;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUTCDATE() {
        return UTCDATE;
    }

    public void setUTCDATE(String UTCDATE) {
        this.UTCDATE = UTCDATE;
    }
}
