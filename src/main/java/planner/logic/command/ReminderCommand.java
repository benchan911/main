//@@author kyawtsan99

package planner.logic.command;

import planner.credential.user.User;
import planner.main.CliLauncher;
import planner.util.crawler.JsonWrapper;
import planner.ui.cli.PlannerUi;
import planner.util.storage.Storage;

import planner.logic.modules.module.ModuleInfoDetailed;

import java.util.HashMap;

import java.util.Timer;
import planner.util.legacy.schedule.ScheduledTask;
import planner.util.legacy.schedule.ModTimer;

public class ReminderCommand extends ModuleCommand {

    public ReminderCommand(Arguments args) {
        super(args);
    }

    /**
     * Prints the reminder message every ten seconds.
     */
    private void printEveryTenSec(PlannerUi plannerUi) throws InterruptedException {
        Timer time = new ModTimer();
        ScheduledTask scheduledTask = new ScheduledTask(plannerUi);
        time.schedule(scheduledTask, 0, 10000);
    }


    /**
     * Prints the reminder message every thirty seconds.
     */
    private void printEveryThirtySec(PlannerUi plannerUi) throws InterruptedException {
        Timer time = new ModTimer();
        ScheduledTask scheduledTask = new ScheduledTask(plannerUi);
        time.schedule(scheduledTask, 0, 30000);
    }

    /**
     * Prints the reminder message every one minute.
     */
    private void printEveryOneMin(PlannerUi plannerUi) throws InterruptedException {
        Timer time = new ModTimer();
        ScheduledTask scheduledTask = new ScheduledTask(plannerUi);
        time.schedule(scheduledTask, 0, 60000);
    }

    /**
     * Prints the reminder message every two minutes.
     */
    private void printEveryTwoMin(PlannerUi plannerUi) throws InterruptedException {
        Timer time = new ModTimer();
        ScheduledTask scheduledTask = new ScheduledTask(plannerUi);
        time.schedule(scheduledTask, 0, 120000);
    }

    /**
     * Stops the reminder message.
     */
    private void killAllTimers() {
        for (Timer timer: CliLauncher.timerPool) {
            timer.cancel();
        }
        System.out.println("_______________________________\n"
                            +
                            "Your reminder for the update is being stopped.\n"
                            +
                            "To activate the reminder again, type reminder list.");
    }

    @Override
    public void execute(HashMap<String, ModuleInfoDetailed> detailedMap,
                        PlannerUi plannerUi,
                        Storage store,
                        JsonWrapper jsonWrapper,
                        User profile) {

        switch (arg("toReminder")) {
            case ("list"): {
                plannerUi.reminderList();
                break;
            }

            case ("one"): {
                try {
                    printEveryTenSec(plannerUi);
                } catch (InterruptedException e) {
                    plannerUi.println(e.getMessage());
                }
                break;
            }

            case ("two"): {
                try {
                    printEveryThirtySec(plannerUi);
                } catch (InterruptedException e) {
                    plannerUi.println(e.getMessage());
                }
                break;
            }

            case ("three"): {
                try {
                    printEveryOneMin(plannerUi);
                } catch (InterruptedException e) {
                    plannerUi.println(e.getMessage());
                }
                break;
            }

            case ("four"): {
                try {
                    printEveryTwoMin(plannerUi);
                } catch (InterruptedException e) {
                    plannerUi.println(e.getMessage());
                }
                break;
            }

            case ("stop"):
            default: {
                killAllTimers();
            }

        }
    }
}
