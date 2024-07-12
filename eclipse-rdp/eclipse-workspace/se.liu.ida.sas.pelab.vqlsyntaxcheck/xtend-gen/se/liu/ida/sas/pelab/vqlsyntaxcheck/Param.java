package se.liu.ida.sas.pelab.vqlsyntaxcheck;

@SuppressWarnings("all")
public class Param {
  private final String key;

  private final String value;

  public Param(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return this.key;
  }

  public String asString() {
    return this.value;
  }

  public int asInt() {
    return Integer.parseInt(this.value);
  }

  public double asDouble() {
    return Double.parseDouble(this.value);
  }

  public boolean asBoolean() {
    return Boolean.parseBoolean(this.value);
  }

  @Override
  public int hashCode() {
    return this.key.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if ((o instanceof Param)) {
      return (this.key.equals(((Param)o).key) && 
        this.value.equals(((Param)o).value));
    }
    return false;
  }
}
