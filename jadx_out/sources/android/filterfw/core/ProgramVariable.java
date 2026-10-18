package android.filterfw.core;

/* JADX INFO: loaded from: classes.dex */
public class ProgramVariable {
    private Program mProgram;
    private String mVarName;

    public ProgramVariable(Program program, String str) {
        this.mProgram = program;
        this.mVarName = str;
    }

    public Program getProgram() {
        return this.mProgram;
    }

    public Object getValue() {
        if (this.mProgram != null) {
            return this.mProgram.getHostValue(this.mVarName);
        }
        throw new RuntimeException("Attempting to get program variable '" + this.mVarName + "' but the program is null!");
    }

    public String getVariableName() {
        return this.mVarName;
    }

    public void setValue(Object obj) {
        if (this.mProgram != null) {
            this.mProgram.setHostValue(this.mVarName, obj);
            return;
        }
        throw new RuntimeException("Attempting to set program variable '" + this.mVarName + "' but the program is null!");
    }
}
