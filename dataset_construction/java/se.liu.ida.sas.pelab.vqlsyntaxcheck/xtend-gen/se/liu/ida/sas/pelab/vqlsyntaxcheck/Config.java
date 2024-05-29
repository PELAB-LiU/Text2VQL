package se.liu.ida.sas.pelab.vqlsyntaxcheck;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;

@SuppressWarnings("all")
public class Config extends HashMap<String, Param> {
  public String asString(final String key) {
    return this.get(key).asString();
  }

  public int asInt(final String key) {
    return this.get(key).asInt();
  }

  public double asDouble(final String key) {
    return this.get(key).asDouble();
  }

  public boolean asBoolean(final String key) {
    return this.get(key).asBoolean();
  }

  public boolean isDefined(final String key) {
    return this.containsKey(key);
  }

  public Config(final String[] args) {
    for (final String arg : args) {
      boolean _startsWith = arg.startsWith("#");
      boolean _not = (!_startsWith);
      if (_not) {
        final String[] values = arg.split("=", 2);
        int _size = ((List<String>)Conversions.doWrapArray(values)).size();
        boolean _tripleEquals = (_size == 2);
        if (_tripleEquals) {
          String _get = values[0];
          String _get_1 = values[1];
          final Param param = new Param(_get, _get_1);
          this.put(param.getKey(), param);
        } else {
          String _get_2 = values[0];
          final Param param_1 = new Param(_get_2, "true");
          this.put(param_1.getKey(), param_1);
        }
      }
    }
  }

  public static Timer kill(final long sec) {
    final Timer timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        System.exit(0);
      }
    }, 
      (sec * 1000));
    return timer;
  }

  public static Timer timeout(final int sec, final Procedure0 then) {
    final Timer timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        then.apply();
      }
    }, 
      (sec * 1000));
    return timer;
  }
}
