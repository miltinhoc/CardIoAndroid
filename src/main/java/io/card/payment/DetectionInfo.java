//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

class DetectionInfo {
    public boolean complete = false;
    public boolean topEdge;
    public boolean bottomEdge;
    public boolean leftEdge;
    public boolean rightEdge;
    public float focusScore;
    public int[] prediction = new int[16];
    public int expiry_month;
    public int expiry_year;
    public CreditCard detectedCard;

    public DetectionInfo() {
        this.prediction[0] = -1;
        this.prediction[15] = -1;
        this.detectedCard = new CreditCard();
    }

    boolean sameEdgesAs(DetectionInfo other) {
        return other.topEdge == this.topEdge && other.bottomEdge == this.bottomEdge && other.leftEdge == this.leftEdge && other.rightEdge == this.rightEdge;
    }

    boolean detected() {
        return this.topEdge && this.bottomEdge && this.rightEdge && this.leftEdge;
    }

    boolean predicted() {
        return this.complete;
    }

    CreditCard creditCard() {
        String numberStr = new String();

        for(int i = 0; i < 16 && 0 <= this.prediction[i] && this.prediction[i] < 10; ++i) {
            numberStr = numberStr + String.valueOf(this.prediction[i]);
        }

        this.detectedCard.cardNumber = numberStr;
        this.detectedCard.expiryMonth = this.expiry_month;
        this.detectedCard.expiryYear = this.expiry_year;
        return this.detectedCard;
    }

    int numVisibleEdges() {
        return (this.topEdge ? 1 : 0) + (this.bottomEdge ? 1 : 0) + (this.leftEdge ? 1 : 0) + (this.rightEdge ? 1 : 0);
    }
}
