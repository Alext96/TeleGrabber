import java.awt.*;
import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import static org.osbot.rs07.script.MethodProvider.random;
import static org.osbot.rs07.script.MethodProvider.sleep;

import org.osbot.rs07.api.ui.MagicSpell;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "Alex", info = "Telegrab Nature Runes", name = "TeleGrabber v1", version = 0, logo = "")

public class TeleGrabber extends Script {

    public static Spells.NormalSpells TELEKINETIC_GRAB;
    private Script script;
    Area closestBank;

    @Override
    public void onStart() {
        log("välkommen till Telegrab scriptet");
        log("startar...");
    }

    private enum State {
        DEPOSIT, TELEGRAB, WALKTOWILDERNESS, WORLDSWITCH, WAIT
    };

    private State getState()

    {
        GroundItem item = getGroundItems().closest("Nature Rune");

        if(!inventory.contains(563)) //LAWRUNE
        {
            return State.DEPOSIT;
        }

        if((item != null && inventory.contains(563)))
        {
            return State.TELEGRAB;
        }

        if(inventory.contains(563))
        {
            return State.WALKTOWILDERNESS;
        }

        return State.WORLDSWITCH;
    }

    @Override
    public int onLoop() throws InterruptedException {
        getState();
        switch (getState()) {
            case DEPOSIT:
                Banking banking = new Banking(script);
                Bank bank = new Bank();
                banking.getClosestBank(closestBank);
                banking.runToBank(closestBank);
                banking.openClosestBank();
                bank.depositAll();
                bank.withdraw(563, 100);
                bank.withdraw(361, 10);
                getState();
                break;

            case WALKTOWILDERNESS:
                //WalkingEvent walkingEvent = new WalkingEvent((3303, 3859, 0));
                //walkingEvent.setMinDistanceThreshold(0);
                // execute(walkingEvent);
                Position tile = new Position(3303, 3859, 0);
                tile.interact(bot, "Walk here");
                sleep(random(250, 1000));
                getState();
                break;

            case TELEGRAB:
                GroundItem item = getGroundItems().closest("Nature Rune");
                MagicSpell spellToCast = TELEKINETIC_GRAB;
                if (item != null && getMagic().canCast(spellToCast)) {
                    getMagic().castSpellOnEntity(spellToCast, item);
                    getMagic().castSpellOnEntity(TELEKINETIC_GRAB, item);
                    break;
                }

            case WORLDSWITCH:
                sleep(random(250, 5000));
                worlds.hopToF2PWorld();
                break;

            case WAIT:
                sleep(random(250, 5000));
                break;

        }
        return random(700, 1000);
    }

    @Override
    public void onExit() {
        //here i can log how many goblins i have killed, how long i ran script for, logging purposes
        log("bajs");
    }

    @Override
    public void onPaint(Graphics2D g) {
        //for displaying GFX/PAINT DISPLAY INFO ON SCREEN
    }

}
