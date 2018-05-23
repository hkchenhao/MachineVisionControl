#include "MainFormEvent.h"

MainFormEvent::MainFormEvent(MainFormEventType eventtype, CameraIdentifier cameraid) : QEvent(static_cast<QEvent::Type>(CustomEventType_MainForm))
{
    eventtype_ = eventtype;
    cameraid_ = cameraid;
}



