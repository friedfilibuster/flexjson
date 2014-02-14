package flexjson.mock.superhero;

public class XRayVision implements SuperPower {

    private float power;

    protected XRayVision() {
    }

    public XRayVision(float power) {
        this.power = power;
    }

    public float getPower() {
        return power;
    }

    protected void setPower(float power) {
        this.power = power;
    }
}
