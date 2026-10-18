package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.ICarFragrance;

/* JADX INFO: loaded from: classes4.dex */
public class CarFragranceManager extends BaseApiManager implements ICarFragrance {
    private static final String TAG = "CarFragranceManager";
    private static CarFragranceManager sMe;

    public static abstract class CarFragranceListener {
        @SignalReceiver(signalId = 547)
        public void onFragrance1WorkStateChanged(ICarFragrance.FragranceWorkState fragranceWorkState) {
        }

        @SignalReceiver(signalId = 548)
        public void onFragrance2WorkStateChanged(ICarFragrance.FragranceWorkState fragranceWorkState) {
        }

        @SignalReceiver(signalId = 549)
        public void onFragrance3WorkStateChanged(ICarFragrance.FragranceWorkState fragranceWorkState) {
        }

        @SignalReceiver(signalId = 550)
        public void onFragranceTaste1FragChanged(int i) {
        }

        @SignalReceiver(signalId = 544)
        public void onFragranceTaste1PercentChanged(int i) {
        }

        @SignalReceiver(signalId = 551)
        public void onFragranceTaste2FragChanged(int i) {
        }

        @SignalReceiver(signalId = 545)
        public void onFragranceTaste2PercentChanged(int i) {
        }

        @SignalReceiver(signalId = 552)
        public void onFragranceTaste3FragChanged(int i) {
        }

        @SignalReceiver(signalId = 546)
        public void onFragranceTaste3PercentChanged(int i) {
        }
    }

    private CarFragranceManager() {
    }

    public static synchronized CarFragranceManager get() {
        CarFragranceManager carFragranceManager;
        if (sMe != null) {
            carFragranceManager = sMe;
        } else {
            carFragranceManager = new CarFragranceManager();
            sMe = carFragranceManager;
        }
        return carFragranceManager;
    }

    public ICarFragrance.FragranceWorkState getFragrance1WorkState() {
        return ICarFragrance.FragranceWorkState.valueOf(getSignalValue(547).intValue());
    }

    public ICarFragrance.FragranceWorkState getFragrance2WorkState() {
        return ICarFragrance.FragranceWorkState.valueOf(getSignalValue(548).intValue());
    }

    public ICarFragrance.FragranceWorkState getFragrance3WorkState() {
        return ICarFragrance.FragranceWorkState.valueOf(getSignalValue(549).intValue());
    }

    public int getFragranceTaste1Frag() {
        return getSignalValue(550).intValue();
    }

    public int getFragranceTaste1Percent() {
        return getSignalValue(544).intValue();
    }

    public int getFragranceTaste2Frag() {
        return getSignalValue(551).intValue();
    }

    public int getFragranceTaste2Percent() {
        return getSignalValue(545).intValue();
    }

    public int getFragranceTaste3Frag() {
        return getSignalValue(552).intValue();
    }

    public int getFragranceTaste3Percent() {
        return getSignalValue(546).intValue();
    }

    public void registerListener(CarFragranceListener carFragranceListener) {
        registerReceiver(carFragranceListener);
    }

    public boolean setFRAGRANCE_CONCENTRATION(ICarFragrance.FragranceConcentration fragranceConcentration) {
        return setSignalValue(-1, fragranceConcentration.value);
    }

    public boolean setFragranceTaste(ICarFragrance.FragranceTaste fragranceTaste) {
        return setSignalValue(542, fragranceTaste.value);
    }

    public void unregisterListener(CarFragranceListener carFragranceListener) {
        unregisterReceiver(carFragranceListener);
    }
}
