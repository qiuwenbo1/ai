package org.qwb.ai.faceRecognition.insight;


import java.util.List;

public class InsightCompareEngine {

    public double compare(List<Float> feature1, List<Float> feature2) {
        if (feature1.size() > feature2.size()) {
            int temp = feature1.size() - feature2.size();
            for (int i = 0; i < temp; i++) {
                feature2.add(0f);
            }
        } else if (feature1.size() < feature2.size()) {
            int temp = feature2.size() - feature1.size();
            for (int i = 0; i < temp; i++) {
                feature1.add(0f);
            }
        }

        int size = feature1.size();
        double simVal = 0;


        double num = 0;
        double den = 1;
        double powa_sum = 0;
        double powb_sum = 0;
        for (int i = 0; i < size; i++) {
            double a = Double.parseDouble(feature1.get(i).toString());
            double b = Double.parseDouble(feature2.get(i).toString());

            num = num + a * b;
            powa_sum = powa_sum + (double) Math.pow(a, 2);
            powb_sum = powb_sum + (double) Math.pow(b, 2);
        }
        double sqrta = (double) Math.sqrt(powa_sum);
        double sqrtb = (double) Math.sqrt(powb_sum);
        den = sqrta * sqrtb;

        simVal = num / den;

        return simVal;
    }

}
