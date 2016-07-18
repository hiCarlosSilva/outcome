package pro.outcome.data;


public abstract class ActiveInstance<E extends ActiveEntity<?>> extends Instance<E> {

	public Boolean getActive() { return getValue(getEntity().active); }
	public ActiveInstance<E> setActive(Boolean value) { setValue(getEntity().active, value); return this; }
}
