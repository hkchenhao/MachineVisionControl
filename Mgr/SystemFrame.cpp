#include "SystemFrame.h"

SystemFrame::SystemFrame()
{
    // 全局指针初始化
    p_netserver_ = nullptr;
    p_netthread_ = nullptr;
    for(int i = 0; i < CAMERA_NUM; i++)
    {
        p_netthread_buf_[i] = nullptr;
    }
}

SystemFrame::~SystemFrame()
{

}

