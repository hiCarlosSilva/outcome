package pro.outcome.data;


public abstract class DeletableInstance<E extends DeletableEntity<?>> extends Instance<E> {

	public Boolean getDeleted() { return getValue(getEntity().deleted); }
	public DeletableInstance<E> setDeleted(Boolean deleted) { setValue(getEntity().deleted, deleted); return this; }
}
