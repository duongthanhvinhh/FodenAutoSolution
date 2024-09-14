package org.foden.enums;

public enum TestGroups {
    SMOKE(Id.SMOKE), REGRESSION(Id.REGRESSION), BUG(Id.BUG);
    TestGroups(String group){}

    public static class Id {
        public static final String SMOKE = "SMOKE";
        public static final String REGRESSION = "REGRESSION";
        public static final String BUG = "BUG";

    }

}
