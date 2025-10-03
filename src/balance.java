public class balance {
    public double Currentbalance;

    public balance(double Currentbalance) {
        this.Currentbalance = Currentbalance;
    }

    public balance() {
        Currentbalance = 0.0;
    }

    public void addMoney(double amount) {
        Currentbalance += amount;
    }

    public void subtractMoney(double amount) {
        Currentbalance -= amount;
    }


    public void setCurrentbalance(double Currentbalance) {
        this.Currentbalance = Currentbalance;
    }

    public double getCurrentbalance() {
        return Currentbalance;
    }
}
