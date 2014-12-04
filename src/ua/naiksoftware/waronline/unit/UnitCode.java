package ua.naiksoftware.waronline.unit;

public class UnitCode {

    public static final int STEP_ANIM = 9;

    public static final int ID_UP = 0;
    public static final int ID_RIGHT_UP = 1;
    public static final int ID_RIGHT = 2;
    public static final int ID_RIGHT_DOWN = 3;
    public static final int ID_DOWN = 4;
    public static final int ID_LEFT_DOWN = 5;
    public static final int ID_LEFT = 6;
    public static final int ID_LEFT_UP = 7;
    public static final int ID_DIED = 8;

    // минирование, разминирование, обнаружение мин, лечение, восстановление мостов,
    // проходимость слабая, скорость большая на дороге
    public static final int ING_AVTO = 1;

    // проходимость большая (вода), слабо стреляет, из близка нормально, в укрытии слабо уязвим
    public static final int SOLDIER = ING_AVTO + STEP_ANIM;

    // проходимость большая (вода), нормально стреляет, броня слабая
    public static final int HORSE = SOLDIER + STEP_ANIM;

    // проходимость малая, броня средняя, урон средний, дистанция стрельбы короткая
    public static final int HOTCHKISS = HORSE + STEP_ANIM;

    // проходимость большая, броня слабая, урон большой, дистанция стрельбы средняя
    public static final int T34_85 = HOTCHKISS + STEP_ANIM;

    // проходимость средняя, броня средняя, урон средний, дистанция стрельбы средняя
    public static final int PANZER = T34_85 + STEP_ANIM;

    // проходимость большая, броня большая, но слабая, урон большой, дистанция стрельбы большая, борта уязвимые
    public static final int TIGER = PANZER + STEP_ANIM;

    // проходимость низкая, броня средняя, урон самый большой дистанция стрельбы самая большая
    public static final int ARTILLERY = TIGER + STEP_ANIM;
}
