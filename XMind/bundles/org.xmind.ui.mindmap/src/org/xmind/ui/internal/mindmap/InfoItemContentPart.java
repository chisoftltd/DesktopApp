package org.xmind.ui.internal.mindmap;

import org.xmind.core.ITopic;
import org.xmind.gef.part.IPart;
import org.xmind.ui.internal.decorators.InfoItemContentDecorator;
import org.xmind.ui.mindmap.IInfoPart;
import org.xmind.ui.mindmap.ITopicPart;

public class InfoItemContentPart extends TitleTextPart {

    public InfoItemContentPart() {
        setDecorator(InfoItemContentDecorator.getInstance());
    }

    public String getContent() {
        if (getModel() instanceof InfoItemContent) {
            return ((InfoItemContent) getModel()).getContent();
        }
        return null;
    }

    public ITopic getTopic() {
        return (ITopic) super.getRealModel();
    }

    public ITopicPart getTopicPart() {
        if (getParent() instanceof IInfoPart)
            return ((IInfoPart) getParent()).getTopicPart();
        return null;
    }

    @Override
    public void setParent(IPart parent) {
        if (getParent() instanceof InfoPart)
            ((InfoPart) getParent()).removeInfoItemContent(this);
        super.setParent(parent);
        if (getParent() instanceof InfoPart)
            ((InfoPart) getParent()).addInfoItemContent(this);
    }

    @Override
    protected void updateView() {
        super.updateView();
        updateToolTip();
    }

}
