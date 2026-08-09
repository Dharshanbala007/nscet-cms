package com.nscet.cms.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public class ReportManager {

    public static void printReport(String reportName, Collection<?> data, Map<String, Object> params) {
        try {
            InputStream reportStream = ReportManager.class.getResourceAsStream("/jasper/" + reportName + ".jrxml");
            if (reportStream == null) {
                throw new RuntimeException("Report not found: " + reportName);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception e) {
            throw new RuntimeException("Error generating report: " + reportName, e);
        }
    }

    public static void printReportDirectly(String reportName, Collection<?> data, Map<String, Object> params) {
        try {
            InputStream reportStream = ReportManager.class.getResourceAsStream("/jasper/" + reportName + ".jrxml");
            if (reportStream == null) {
                throw new RuntimeException("Report not found: " + reportName);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (Exception e) {
            throw new RuntimeException("Error printing report: " + reportName, e);
        }
    }

    public static byte[] exportToPdf(String reportName, Collection<?> data, Map<String, Object> params) {
        try {
            InputStream reportStream = ReportManager.class.getResourceAsStream("/jasper/" + reportName + ".jrxml");
            if (reportStream == null) {
                throw new RuntimeException("Report not found: " + reportName);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting report to PDF: " + reportName, e);
        }
    }
}
