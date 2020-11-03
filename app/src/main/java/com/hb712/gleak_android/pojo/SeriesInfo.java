package com.hb712.gleak_android.pojo;

/**
 * 曲线信息
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 10:00
 */
public class SeriesInfo {
    private String seriesName = "";
    private boolean stdSeries = false;
    private Long id;

    public SeriesInfo() {
    }

    public SeriesInfo(Long id, String seriesName, boolean stdSeries) {
        this.id = id;
        this.seriesName = seriesName;
        this.stdSeries = stdSeries;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public boolean isStdSeries() {
        return stdSeries;
    }

    public void setStdSeries(boolean stdSeries) {
        this.stdSeries = stdSeries;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
