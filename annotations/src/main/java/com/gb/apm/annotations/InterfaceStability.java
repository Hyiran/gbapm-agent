package com.gb.apm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copy from hadoop-annotations <br>
 * https://github.com/apache/hadoop/tree/trunk/hadoop-common-project/hadoop-annotations/src/main/java/org/apache/hadoop/classification <br><br>
 *
 * Annotation to inform users of how much to rely on a particular package,
 * class or method not changing over time. Currently the stability can be
 * {@link Stable}, {@link Evolving} or {@link Unstable}. <br>
 *
 * <ul><li>All classes that are annotated with {@link InterfaceAudience.Public} or
 * {@link InterfaceAudience.LimitedPrivate} must have InterfaceStability annotation. </li>
 * <li>Classes that are {@link InterfaceAudience.Private} are to be considered unstable unless
 * a different InterfaceStability annotation states otherwise.</li>
 * <li>Incompatible changes must not be made to classes marked as stable.</li>
 * </ul>
 */
@InterfaceAudience.Public
@InterfaceStability.Evolving
public class InterfaceStability {
    /**
     * Can evolve while retaining compatibility for minor release boundaries.;
     * can break compatibility only at major release (ie. at m.0).
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Stable {};

    /**
     * Evolving, but can break compatibility at minor release (i.e. m.x)
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Evolving {};

    /**
     * No guarantee is provided as to reliability or stability across any
     * level of release granularity.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Unstable {};
}

