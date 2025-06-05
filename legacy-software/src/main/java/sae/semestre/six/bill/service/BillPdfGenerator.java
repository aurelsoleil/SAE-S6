package sae.semestre.six.bill.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import sae.semestre.six.bill.entity.Bill;
import sae.semestre.six.bill.entity.BillDetail;

import java.io.ByteArrayOutputStream;

public class BillPdfGenerator {
    public static byte[] generatePdf(Bill bill) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("Facture n° " + bill.getBillNumber()));
        document.add(new Paragraph("Date : " + bill.getBillDate()));
        document.add(new Paragraph("Patient : " + bill.getPatient().getFirstName() + " " + bill.getPatient().getLastName()));
        document.add(new Paragraph("Montant total : " + String.format("%.2f €", bill.getTotalAmount())));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.addCell("Prestation");
        table.addCell("Quantité");
        table.addCell("Prix unitaire");

        for (BillDetail detail : bill.getBillDetails()) {
            table.addCell(detail.getTreatmentName());
            table.addCell(String.valueOf(detail.getQuantity() != null ? detail.getQuantity() : 1));
            table.addCell(String.format("%.2f €", detail.getUnitPrice()));
        }
        document.add(table);

        document.close();
        return baos.toByteArray();
    }
}