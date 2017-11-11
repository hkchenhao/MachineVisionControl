#include <QTimer>
#include <QLabel>
#include <QMovie>
#include "LoadDialog.h"
#include "Event/MainFormEvent.h"

LoadDialog::LoadDialog(QWidget* parent, Qt::WindowFlags f) : QDialog(parent, f)
{
    //this->setModal(true);
    //this->setWindowModality(Qt::WindowModal);

    // 对话框关闭自动销毁
    this->setAttribute(Qt::WA_DeleteOnClose);
    this->setFixedSize(500, 50);
    this->setWindowOpacity(0.8);
    //设置背景色为淡蓝色
    this->setStyleSheet("background-color: rgb(55, 135, 215);");
    //取消对话框标题
    this->setWindowFlags(Qt::Dialog | Qt::FramelessWindowHint);

    // gif
    p_loadingLabel = new QLabel(this);
    p_loadingLabel->setGeometry(0, 0, this->height(), this->height());  // 相对于父节点的坐标范围（左上角为原点）
    p_loadingLabel->setAlignment(Qt::AlignCenter);
    p_loadingMovie = new QMovie(":/icon/loading-32px.gif", QByteArray(), this);
    p_loadingLabel->setMovie(p_loadingMovie);
    p_loadingMovie->start();

    // text
    p_loadingText = new QLabel("正在连接中，请稍后......", this);
    p_loadingText->setGeometry(this->height(), 0, this->width() - this->height(), this->height());  // 相对于父节点的坐标范围（左上角为原点）
    p_loadingText->setAlignment(Qt::AlignCenter);

    QTimer::singleShot(3000, this, [this]()
    {
        this->accept();
    });
}

void LoadDialog::customEvent(QEvent* event)
{
//    MainFormEvent* p_mainformevent = static_cast<MainFormEvent*>(event);
//    switch (p_mainformevent->GetMainFormEventType())
//    {
//        case MainFormEvent::TypeCarmera1LinkResult:
//        {
//            bool carmera1_linkresult = p_mainformevent->GetCameraLinkResultEvent(CameraId1);
//            if(carmera1_linkresult == true)
//            {
//                p_loadingText->setText("相机连接成功！");
//            }
//            else
//            {
//                p_loadingText->setText(QString("相机连接失败！请检查网络设置......"));
//            }
//            QTimer::singleShot(1000, this, [this]()
//            {
//                this->accept();
//            });
//            break;
//        }
//        default:
//            break;
//    }
}
