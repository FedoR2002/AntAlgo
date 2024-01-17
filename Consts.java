public interface Consts {
    int magor_v = 0;
    int minor_v = 1;

    // show ant options
    int VIEW_MODE_BOT_BASE = 0;
    int VIEW_MODE_BOT_TRACK = 1;
    int VIEW_MODE_BOT_BOT_TRACK = 2;

    // show map options
    int VIEW_MODE_HEIGHT=0;
    int VIEW_MODE_SOLAR=1;
    int VIEW_MODE_MINERAL=2;
    int VIEW_MODE_RADIATION=3;
    int VIEW_MODE_ORGANICS=4;

    String wmspText = "World size (% of Max):";

    // strain
    int STRAIN_STABLE_THRESHOLD = 200;

    int MAX_BOT_HP = 1000;
    int MIND_SIZE = 64;

    float SUN2HP_COEFF = 0.75f;
    float MIN2HP_COEFF = 1;
    float ORG2HP_COEFF = 1;

    int CSHIFT = 16;
}
