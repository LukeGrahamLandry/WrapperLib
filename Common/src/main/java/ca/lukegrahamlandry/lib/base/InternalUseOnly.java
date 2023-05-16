/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.lib.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates a method or field that is made public for my convenience, but it is probably a bad plan for you to use if just depending on the library.
 * Often these will be unsafe because they do weird things with unchecked casts of objects to generics that are used for syncing where we trust that the server sent good information but would be easy to make a mistake when using from general code.
 * Also on things that I don't consider part of the public contract regarding breaking changes.
 */
@Retention(RetentionPolicy.CLASS)
public @interface InternalUseOnly {
}
