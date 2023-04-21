package dev.voidframework.core.classestoload.classes;

import dev.voidframework.core.proxyable.Proxyable;

@Proxyable
public interface ProxInterface {

    long countByFirstNameLike(final String likePattern);
}
