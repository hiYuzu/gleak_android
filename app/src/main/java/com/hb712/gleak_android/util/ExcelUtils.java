package com.hb712.gleak_android.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/25 10:24
 */
public class ExcelUtils {
    private static final String TAG = ExcelUtils.class.getSimpleName();

    public static WritableFont arial14font = null;
    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";


    /**
     * 初始化Excel
     *
     * @param fileName 文件名
     * @param colName  列名
     */
    public static String initExcel(String fileName, String[] colName) throws Exception {
        format();
        WritableWorkbook workbook = null;
        File file;
        String absFilePath;
        try {
            file = initExcelFile(fileName);
            absFilePath = file.getAbsolutePath();
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("历史记录", 0);
            //创建标题栏
            sheet.addCell(new Label(0, 0, absFilePath, arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            // 设置行高
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            throw new Exception("初始化 Excel 失败");
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     * @throws WriteException 创建字体格式异常
     */
    private static void format() throws WriteException {
        arial14font = new WritableFont(WritableFont.TIMES, 14, WritableFont.BOLD);
        arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);

        arial14format = new WritableCellFormat(arial14font);
        arial14format.setAlignment(jxl.format.Alignment.CENTRE);
        arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);

        arial10font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);

        arial10format = new WritableCellFormat(arial10font);
        arial10format.setAlignment(jxl.format.Alignment.CENTRE);
        arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        arial10format.setBackground(Colour.GRAY_25);
        // 对齐格式
        arial10format.setAlignment(jxl.format.Alignment.CENTRE);

        arial12font = new WritableFont(WritableFont.TIMES, 10);

        arial12format = new WritableCellFormat(arial12font);
        // 设置边框
        arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
    }

    private static File initExcelFile(String fileName) throws IOException {
        String excelFilePath = GlobalParam.EXCEL_PATH + File.separator + fileName;
        File excelFileDir = new File(GlobalParam.EXCEL_PATH);
        if (!excelFileDir.exists()) {
            if (!excelFileDir.mkdirs()) {
                throw new IOException("文件夹创建失败");
            }
        }
        try {
            File file = new File(excelFilePath);
            if (file.createNewFile()) {
                return file;
            }
        } catch (IOException ioe) {
            throw new IOException("文件创建失败");
        }
        return null;
    }

    /**
     * 数据写入到 Excel
     *
     * @param objList  数据
     * @param fileName 完整路径文件名
     * @param <T>      数据格式
     * @throws Exception 输入异常、关闭流异常
     */
    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList, String fileName) throws Exception {
        WorkbookSettings setEncode = new WorkbookSettings();
        setEncode.setEncoding(UTF8_ENCODING);
        InputStream in = new FileInputStream(new File(fileName));
        Workbook workbook = Workbook.getWorkbook(in);
        WritableWorkbook writeBook = Workbook.createWorkbook(new File(fileName), workbook);
        WritableSheet sheet = writeBook.getSheet(0);

        for (int j = 0; j < objList.size(); j++) {
            ArrayList<String> list = (ArrayList<String>) objList.get(j);
            for (int i = 0; i < list.size(); i++) {
                sheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                if (list.get(i).length() <= 5) {
                    // 设置列宽
                    sheet.setColumnView(i, list.get(i).length() + 8);
                } else {
                    // 设置列宽
                    sheet.setColumnView(i, list.get(i).length() + 5);
                }
            }
            // 设置行高
            sheet.setRowView(j + 1, 350);
        }
        writeBook.write();
        if (writeBook != null) {
            writeBook.close();
        }
        if (in != null) {
            in.close();
        }
    }
}
