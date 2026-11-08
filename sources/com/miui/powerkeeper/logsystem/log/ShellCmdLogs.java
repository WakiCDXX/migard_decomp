package com.miui.powerkeeper.logsystem.log;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.util.Log;
import android.util.SparseArray;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import java.io.PrintWriter;
import java.util.Arrays;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class ShellCmdLogs {
    private static final int MAX_CMD_INDEX = 10;
    private static final int MIN_CMD_CYCLE = 5;
    public static final String TAG = "LS-ShellCmdLogs";
    private Context mContext;
    private SparseArray<CommandInfo> mCommandsArray = null;
    private int mCurrentIndex = 0;
    private volatile boolean mIsRegisterd = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.logsystem.log.ShellCmdLogs.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) throws Throwable {
            CommandInfo commandInfo;
            int intExtra = intent.getIntExtra("cmd_index", -1);
            Log.d(ShellCmdLogs.TAG, "onReceive index:" + intExtra);
            if (ShellCmdLogs.this.mCommandsArray == null || intExtra == -1 || (commandInfo = (CommandInfo) ShellCmdLogs.this.mCommandsArray.get(intExtra)) == null) {
                return;
            }
            ShellCmdLogs.this.execCommand(commandInfo);
            int i2 = commandInfo.executed + 1;
            commandInfo.executed = i2;
            if (i2 < commandInfo.times) {
                ShellCmdLogs.this.rescheduleCommand(commandInfo, intent);
                return;
            }
            ShellCmdLogs.this.mCommandsArray.remove(intExtra);
            if (ShellCmdLogs.this.mCommandsArray.size() == 0) {
                ShellCmdLogs.this.mCommandsArray = null;
                ShellCmdLogs.this.unregisterReceiver();
            }
        }
    };

    class CommandInfo {
        String cmd;
        int cycle;
        int executed = 0;
        String filePath;
        int times;

        CommandInfo() {
        }
    }

    ShellCmdLogs(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void execCommand(CommandInfo commandInfo) throws Throwable {
        Log.d(TAG, "EXEC Command:" + commandInfo.cmd);
        if (!LSUtils.isEmptyString(commandInfo.filePath)) {
            LSUtils.execCommand(commandInfo.cmd, commandInfo.filePath, true);
            return;
        }
        LSUtils.execCommand(commandInfo.cmd, this.mContext.getCacheDir().getPath() + "/shell_cmd_catch", true);
    }

    private boolean isFeatureSupported() {
        if (SystemProperties.getBoolean("debug.ls.shell.cmd", false)) {
            return true;
        }
        return SystemProperties.getBoolean("ro.mi.development", false) && Build.IS_DEBUGGABLE;
    }

    private CommandInfo parseCommands(String[] strArr) {
        CommandInfo commandInfo = new CommandInfo();
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        while (i2 < strArr.length) {
            try {
                if (strArr[i2].equals("-p")) {
                    i2++;
                    if (i2 >= strArr.length) {
                        return commandInfo;
                    }
                    commandInfo.cycle = Integer.valueOf(strArr[i2]).intValue();
                }
                if (strArr[i2].equals("-d")) {
                    i2++;
                    if (i2 >= strArr.length) {
                        return commandInfo;
                    }
                    commandInfo.times = Integer.valueOf(strArr[i2]).intValue();
                }
                if (strArr[i2].equals("-f")) {
                    i2++;
                    if (i2 >= strArr.length) {
                        return commandInfo;
                    }
                    commandInfo.filePath = strArr[i2];
                }
                if (strArr[i2].equals("-c")) {
                    i2++;
                    break;
                }
                i2++;
            } catch (NumberFormatException e2) {
                e2.printStackTrace();
                return null;
            }
        }
        if (commandInfo.times <= 0) {
            return null;
        }
        if (commandInfo.cycle < 5) {
            commandInfo.cycle = 5;
        }
        while (i2 < strArr.length) {
            sb.append(strArr[i2]);
            sb.append(" ");
            i2++;
        }
        commandInfo.cmd = sb.toString();
        return commandInfo;
    }

    private void printHelp(PrintWriter printWriter) {
        printWriter.println("dumpsys activity service com.miui.powerkeeper/.logsystem.LogSystemService -s -p <number> -d <number> [-f <path>] -c <command string>");
        printWriter.println("\t--help   \t打印Help信息");
        printWriter.println("\t--info   \t打印当前已激活的命令信息");
        printWriter.println("\t-p <num> \t新命令执行周期, 单位为秒, 最小为5秒");
        printWriter.println("\t-d <num> \t新命令持续执行次数");
        printWriter.println("\t-f <path>\tresult 存储文件路径, 如果未指定, 则采用默认存储路径: /data/user/0/com.miui.powerkeeper/cache/shell_cmd_catch");
        printWriter.println("\t-c <cmd> \t新添加的命令, 必须作为最后一个参数");
    }

    private void registerReceiver() {
        synchronized (this) {
            try {
                if (!this.mIsRegisterd) {
                    this.mIsRegisterd = true;
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("exec_command");
                    this.mContext.registerReceiver(this.mReceiver, intentFilter, 2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rescheduleCommand(CommandInfo commandInfo, Intent intent) {
        Log.d(TAG, "SCHEDULE Command:" + commandInfo.cmd + " Index:" + intent.getIntExtra("cmd_index", -1));
        ((AlarmManager) this.mContext.getSystemService("alarm")).setExactAndAllowWhileIdle(0, System.currentTimeMillis() + ((long) (commandInfo.cycle * 1000)), PendingIntent.getBroadcast(this.mContext, intent.getIntExtra("cmd_index", -1), intent, 67108864));
    }

    private void scheduleCommand(int i2) {
        Log.d(TAG, "SCHEDULE Command:" + this.mCommandsArray.get(i2).cmd + " Index:" + i2);
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        Intent intent = new Intent();
        intent.setAction("exec_command");
        intent.putExtra("cmd_index", i2);
        alarmManager.setExactAndAllowWhileIdle(0, System.currentTimeMillis(), PendingIntent.getBroadcast(this.mContext, i2, intent, 67108864));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterReceiver() {
        synchronized (this) {
            try {
                if (this.mIsRegisterd) {
                    this.mIsRegisterd = false;
                    this.mContext.unregisterReceiver(this.mReceiver);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void destroy() {
        unregisterReceiver();
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        if (strArr.length < 1) {
            printHelp(printWriter);
            return;
        }
        String[] strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
        if (strArr2.length == 0) {
            printHelp(printWriter);
            return;
        }
        if (strArr2[0].equals("--help")) {
            printHelp(printWriter);
            return;
        }
        if (strArr2[0].equals("--info")) {
            printWriter.println("Current command info.");
            if (this.mCommandsArray == null) {
                printWriter.println("  Cmd list is Null.");
                return;
            }
            printWriter.println("  Cmd list size=" + this.mCommandsArray.size());
            for (int i2 = 0; i2 < 10; i2++) {
                CommandInfo commandInfo = this.mCommandsArray.get(i2);
                if (commandInfo == null) {
                    printWriter.println("  index:" + i2 + "\t Cmd: null");
                } else {
                    printWriter.println("  index:" + i2 + "\t Cycle:" + commandInfo.cycle + "\t Times:" + commandInfo.times + "\t Executed:" + commandInfo.executed + "\t Cmd:" + commandInfo.cmd);
                }
            }
            return;
        }
        if (!isFeatureSupported()) {
            printWriter.println("Failed!! Feature is supported only in debug version.");
            return;
        }
        CommandInfo commands = parseCommands(strArr2);
        if (commands == null || LSUtils.isEmptyString(commands.cmd)) {
            printWriter.println("Add new cmds failed. Please check the params.");
            return;
        }
        printWriter.println("Add new cmds=" + commands.cmd);
        printWriter.println("        cycle=" + commands.cycle);
        printWriter.println("        times=" + commands.times);
        if (this.mCommandsArray == null) {
            registerReceiver();
            this.mCommandsArray = new SparseArray<>();
        }
        for (int i3 = 0; i3 < 10; i3++) {
            if (this.mCommandsArray.get(this.mCurrentIndex) == null) {
                this.mCommandsArray.append(this.mCurrentIndex, commands);
                scheduleCommand(this.mCurrentIndex);
                return;
            } else {
                int i4 = this.mCurrentIndex + 1;
                this.mCurrentIndex = i4;
                if (i4 >= 10) {
                    this.mCurrentIndex = 0;
                }
            }
        }
        printWriter.println("Add Cmd:" + commands.cmd + " failed because the cmd list is full.");
    }
}
