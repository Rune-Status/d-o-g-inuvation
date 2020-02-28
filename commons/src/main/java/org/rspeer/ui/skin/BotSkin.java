package org.rspeer.ui.skin;

import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.SubstanceSlices.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.colorscheme.ColorSchemeSingleColorQuery;
import org.pushingpixels.substance.api.colorscheme.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.CompositeBorderPainter;
import org.pushingpixels.substance.api.painter.border.DelegateBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.FractionBasedFillPainter;
import org.pushingpixels.substance.api.painter.highlight.FractionBasedHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

public final class BotSkin extends SubstanceSkin {

    public static final String NAME = "rspeer";

    public BotSkin() {
        SubstanceSkin.ColorSchemes schemes = SubstanceSkin
                .getColorSchemes("org/rspeer/ui/skin/rspeer.colorschemes");

        SubstanceColorScheme selectedDisabledScheme = schemes
                .get("RSPeer Selected Disabled");
        SubstanceColorScheme selectedScheme = schemes.get("RSPeer Selected");
        SubstanceColorScheme disabledScheme = schemes.get("RSPeer Disabled");

        SubstanceColorScheme enabledScheme = schemes.get("RSPeer Enabled");
        SubstanceColorScheme backgroundScheme = schemes
                .get("RSPeer Background");

        // use the same color scheme for active and enabled controls
        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
                enabledScheme, enabledScheme, disabledScheme);

        // highlight fill scheme + custom alpha for rollover unselected state
        SubstanceColorScheme highlightScheme = schemes.get("RSPeer Aqua");
        defaultSchemeBundle.registerHighlightColorScheme(highlightScheme,
                0.75f, ComponentState.ROLLOVER_UNSELECTED);
        defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 0.9f,
                ComponentState.SELECTED);
        defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1.0f,
                ComponentState.ROLLOVER_SELECTED);
        defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1.0f,
                ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

        defaultSchemeBundle.registerColorScheme(highlightScheme,
                ColorSchemeAssociationKind.BORDER,
                ComponentState.ROLLOVER_ARMED,
                ComponentState.ROLLOVER_SELECTED,
                ComponentState.ROLLOVER_UNSELECTED);
        defaultSchemeBundle.registerColorScheme(highlightScheme,
                ColorSchemeAssociationKind.FILL, ComponentState.SELECTED,
                ComponentState.ROLLOVER_SELECTED);

        // border schemes
        SubstanceColorScheme borderScheme = schemes.get("RSPeer Border");
        SubstanceColorScheme separatorScheme = schemes
                .get("RSPeer Separator");
        defaultSchemeBundle.registerColorScheme(highlightScheme,
                ColorSchemeAssociationKind.HIGHLIGHT_BORDER, ComponentState
                        .getActiveStates());
        defaultSchemeBundle.registerColorScheme(borderScheme,
                ColorSchemeAssociationKind.BORDER);
        defaultSchemeBundle.registerColorScheme(separatorScheme,
                ColorSchemeAssociationKind.SEPARATOR);
        defaultSchemeBundle.registerColorScheme(borderScheme,
                ColorSchemeAssociationKind.MARK);

        // text highlight scheme
        defaultSchemeBundle.registerColorScheme(highlightScheme,
                ColorSchemeAssociationKind.HIGHLIGHT_TEXT,
                ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);

        defaultSchemeBundle.registerColorScheme(highlightScheme,
                ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

        defaultSchemeBundle.registerColorScheme(disabledScheme, 0.5f,
                ComponentState.DISABLED_UNSELECTED);
        defaultSchemeBundle.registerColorScheme(selectedDisabledScheme, 0.5f,
                ComponentState.DISABLED_SELECTED);

        defaultSchemeBundle.registerColorScheme(highlightScheme,
                ComponentState.ROLLOVER_SELECTED);
        defaultSchemeBundle.registerColorScheme(selectedScheme,
                ComponentState.SELECTED);

        SubstanceColorScheme tabHighlightScheme = schemes
                .get("RSPeer Tab Highlight");
        defaultSchemeBundle.registerColorScheme(tabHighlightScheme,
                ColorSchemeAssociationKind.TAB,
                ComponentState.ROLLOVER_SELECTED);

        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
                backgroundScheme, SubstanceSlices.DecorationAreaType.NONE);



        this.setTabFadeStart(0.15);
        this.setTabFadeEnd(0.25);

        this.buttonShaper = new ClassicButtonShaper();
        this.watermark = null;
        this.fillPainter = new FractionBasedFillPainter("RSPeer Aqua",
                new float[]{0.0f, 0.5f, 1.0f},
                new ColorSchemeSingleColorQuery[]{
                        ColorSchemeSingleColorQuery.LIGHT,
                        ColorSchemeSingleColorQuery.MID,
                        ColorSchemeSingleColorQuery.MID});

        this.decorationPainter = new FlatDecorationPainter();
        this.highlightPainter = new FractionBasedHighlightPainter(
                "RSPeer Aqua", new float[]{0.0f, 0.5f, 1.0f},
                new ColorSchemeSingleColorQuery[]{
                        ColorSchemeSingleColorQuery.EXTRALIGHT,
                        ColorSchemeSingleColorQuery.LIGHT,
                        ColorSchemeSingleColorQuery.MID});
        this.borderPainter = new CompositeBorderPainter("RSPeer Aqua",
                new ClassicBorderPainter(), new DelegateBorderPainter(
                "RSPeer Aqua Inner", new ClassicBorderPainter(),
                0xC0FFFFFF, 0x90FFFFFF, 0x30FFFFFF,
                scheme -> scheme.tint(0.25f)));
        this.highlightBorderPainter = new ClassicBorderPainter();
    }

    public String getDisplayName() {
        return NAME;
    }
}
