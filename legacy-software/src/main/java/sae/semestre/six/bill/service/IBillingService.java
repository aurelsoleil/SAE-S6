package sae.semestre.six.bill.service;

public interface IBillingService  {

    void processBill(String patientId, String source, String[] items);

    public double calculateInsurance(double montant);

}
