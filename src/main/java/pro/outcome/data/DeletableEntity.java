package pro.outcome.data;


public abstract class DeletableEntity<I extends DeletableInstance<?>> extends Entity<I> {

	public final Property<Boolean> deleted;

	protected DeletableEntity() {
		deleted = addProperty(Boolean.class, "deleted", true, Boolean.FALSE);
	}
}
