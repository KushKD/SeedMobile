package seeding.mobile.enrolment.aadhaar.hp.dit.aadhaarmobileseeding;

/**
 * Created by kuush on 12/30/2015.
 */
public enum CustomPagerEnum {

    RED(R.layout.view_one),
    BLUE(R.layout.view_two),
    ORANGE(R.layout.view_three);

    private int mTitleResId;
    private int mLayoutResId;

    CustomPagerEnum(int layoutResId) {
      //  mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    /*public int getTitleResId() {
        return mTitleResId;
    }*/

    public int getLayoutResId() {
        return mLayoutResId;
    }
}
