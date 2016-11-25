package ru.oboturov_corp.dogenotes2;

import android.view.animation.Interpolator;

class MyInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float x) {
        //a - отвечает с дальность конечного положения, стандартно 1
        double a = 0;
        //b - регулирует длинну возвратного пути, стандартно 4
        double b = 5;
        //c = ругелирует длинну первоначального подлета, стандартно 5
        double c = 6;
        //d - реуглирует пложение точки начала анимации, стандартно -1
        //чем больше, тем дальше по направлению движения начнетса анимация
        //-1 говорит объекту начинать анимацию со своего обычного положения
        double d = -1.2;
        //e - говорит на сколько сдвинутся в певоначальном направлении перед концом анимации
        double y = a + (b + c * (d + x)) * Math.pow(-0.9 + x, 2);
        return (float) y;
    }
}
