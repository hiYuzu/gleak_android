package com.hb712.gleak_android.util;

import android.content.Context;

import com.hb712.gleak_android.adapter.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/23 16:46
 */
public class Exporter {
    public void exportToExcel(String fileName, List<HistoryAdapter.DetectInfoView> source) throws Exception {
        String[] column = new String[4];
        column[0] = "序号";
        column[1] = "漏点名称";
        column[2] = "检测值";
        column[3] = "检测时间";
        List<List<String>> lines = new ArrayList<>();
        for (HistoryAdapter.DetectInfoView temp : source) {
            List<String> line = new ArrayList<>();
            line.add(String.valueOf(temp.number));
            if (temp.detectInfo != null) {
                line.add(temp.detectInfo.getLeakName());
                line.add(String.valueOf(temp.detectInfo.getMonitorValue()));
                line.add(String.valueOf(temp.detectInfo.getMonitorTime()));
            } else {
                line.add(" ");
                line.add(" ");
                line.add(" ");
            }
            lines.add(line);
        }
        String absFilePath = ExcelUtils.initExcel(fileName, column);
        ExcelUtils.writeObjListToExcel(lines, absFilePath);
    }

}