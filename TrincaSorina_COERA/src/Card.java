import java.time.LocalDate;

public class Card implements Comparable<Card>{
    private float fee;
    private float withdrawLimit;
    private LocalDate expirationDate;
    private float amount;
    private CardType type;

    public Card() {
    }

    public Card(float fee, float withdrawLimit, LocalDate expirationDate, float amount, CardType type) {
        this.fee = fee;
        this.withdrawLimit = withdrawLimit;
        this.expirationDate = expirationDate;
        this.amount = amount;
        this.type = type;
    }

    @Override
    public int compareTo(Card other) {
        return Float.compare(this.fee, other.getFee());
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getWithdrawLimit() {
        return withdrawLimit;
    }

    public void setWithdrawLimit(float withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }
}
