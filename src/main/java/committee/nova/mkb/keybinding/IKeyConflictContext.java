package committee.nova.mkb.keybinding;

/**
 * Defines the context that a {@link net.minecraft.client.options.KeyBinding} is used.
 * Key conflicts occur when a {@link net.minecraft.client.options.KeyBinding} has the same {@link IKeyConflictContext} and has conflicting modifiers and keyCodes.
 */
public interface IKeyConflictContext {
    /**
     * @return true if conditions are met to activate {@link net.minecraft.client.options.KeyBinding}s with this context
     */
    boolean isActive();

    /**
     * @return true if the other context can have {@link net.minecraft.client.options.KeyBinding} conflicts with this one.
     * This will be called on both contexts to check for conflicts.
     */
    boolean conflicts(IKeyConflictContext other);
}
