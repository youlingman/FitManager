package com.cyl.fitmanager.Model;

import com.cyl.fitmanager.Controller.ProgramConfigController;
import com.cyl.fitmanager.Controller.ProgramStatsController;

/**
 * 训练上下文单例
 * Created by Administrator on 2017-7-10.
 */
public class ProgramContext {
    private String program;
    private ProgramConfigController programConfigController;
    private ProgramStatsController programStatsController;
    static private ProgramContext programContext;

    private ProgramContext() {}

    private ProgramContext(String program) {
        programConfigController = ProgramConfigController.getInstance(program);
        programStatsController = ProgramStatsController.getInstance(program);
    }

    static public synchronized void setProgram(String program) {
        if(null == programContext) {
            programContext = new ProgramContext(program);
        } else {
            programContext.programConfigController.changeProgram(program);
            programContext.programStatsController.changeProgram(program);
        }
        programContext.program = program;
    }

    static public ProgramContext getInstance() {
        return programContext;
    }

    public ProgramConfigController getConfig() {
        return this.programConfigController;
    }

    public ProgramStatsController getStats() {
        return this.programStatsController;
    }

    public String getProgram() {
        return program;
    }
}
