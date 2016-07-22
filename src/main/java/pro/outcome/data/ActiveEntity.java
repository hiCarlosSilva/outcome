package pro.outcome.data;


public abstract class ActiveEntity<I extends ActiveInstance<?>> extends Entity<I> {

	public final Property<Boolean> active;

	protected ActiveEntity() {
		active = addProperty(Boolean.class, "active", true, Boolean.TRUE);
	}
}
