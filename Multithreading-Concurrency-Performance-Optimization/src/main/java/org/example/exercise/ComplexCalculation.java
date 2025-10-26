package org.example.exercise;

import java.math.BigInteger;

public class ComplexCalculation {
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) throws InterruptedException {
        BigInteger result;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */

        // Use the concrete thread type so we can call getResult()
        PowerCalculatingThread firstThread = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread secondThread = new PowerCalculatingThread(base2, power2);

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();


        // BigInteger addition uses add()
        result = firstThread.getResult().add(secondThread.getResult());
        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {

            // Start from 1 (multiplicative identity), not 0
            BigInteger tempResult = BigInteger.ONE;
            // iterate power times
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) < 0; i = i.add(BigInteger.ONE)){
                tempResult = tempResult.multiply(base);
            }

            result = tempResult;
        }

        public BigInteger getResult() { return result; }
    }
}
