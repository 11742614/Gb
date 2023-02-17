package com.trs.gb.tools;

import cn.afterturn.easypoi.excel.export.styler.AbstractExcelExportStyler;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import org.apache.poi.ss.usermodel.*;

/**
 * @Auther: guodongfeng
 * @Date: 2021/8/20 16:14
 * @Description:
 */
public class ExcelExportMyStyler extends AbstractExcelExportStyler implements IExcelExportStyler {
    public ExcelExportMyStyler(Workbook workbook) {
        super.createStyles(workbook);
    }

    @Override
    public CellStyle getTitleStyle(short color) {
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);// 加粗
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);// 居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        titleStyle.setFillForegroundColor(IndexedColors.AQUA.index);// 设置颜色
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setWrapText(true);
        return titleStyle;
    }

    @SuppressWarnings("deprecation")
    @Override
    public CellStyle stringSeptailStyle(Workbook workbook, boolean isWarp) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setDataFormat(STRING_FORMAT);
        if (isWarp) {
            style.setWrapText(true);
        }
        return style;
    }

    @Override
    public CellStyle getHeaderStyle(short color) {
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);// 加粗
        font.setColor(IndexedColors.RED.index);
        font.setFontHeightInPoints((short) 11);
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);// 居中
        titleStyle.setFillForegroundColor(IndexedColors.WHITE.index);// 设置颜色
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setWrapText(true);
        return titleStyle;
    }

    @SuppressWarnings("deprecation")
    @Override
    public CellStyle stringNoneStyle(Workbook workbook, boolean isWarp) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setDataFormat(STRING_FORMAT);
        if (isWarp) {
            style.setWrapText(true);
        }
        return style;
    }
}
