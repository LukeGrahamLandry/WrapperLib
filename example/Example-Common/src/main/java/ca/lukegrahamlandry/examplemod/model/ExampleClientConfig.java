/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.model;

import ca.lukegrahamlandry.lib.config.Comment;

public class ExampleClientConfig {
    @Comment("colour of the text that displays your kills on the hud")
    public int uiColour = 0xFF0000;
}
