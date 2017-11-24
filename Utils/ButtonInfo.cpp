#include "ButtonInfo.h"
#include "Utils/QJsonAnalysis.h"

// 图像的类型格式
const QString IMAGE_TYPE(".jpg");

ButtonInfo::ButtonInfo(const QString& filepath, const QString& filename, QWidget* parent) : QWidget(parent)
{
    // 注意 ini文件要以UTF8-BOM编码方式保存
    // 初始化配置文件所在路径与名称
    p_configFilePath = new QString(filepath);
    p_configFileName = new QString(filename);
    // 读取纽扣配置参数ini文件
    p_buttonInfo = new QJsonAnalysis(*p_configFilePath + "/" + *p_configFileName + ".ini", true);
    // 初始化p_buttonImage、p_buttonName与p_layout
    p_layout = new QVBoxLayout;
    p_buttonImage = new QLabel;
    p_buttonName = new QLabel(p_configFileName->section('.', 0, 0));
    p_buttonImage->installEventFilter(this);
    p_buttonName->installEventFilter(this);
    // 配置label布局与属性
    ConfigButtonInfoWidget();
}

ButtonInfo::~ButtonInfo()
{
    if(p_configFilePath) { delete p_configFilePath; }
    if(p_configFileName) { delete p_configFileName; }
}

// 配置label属性
void ButtonInfo::ConfigButtonInfoWidget()
{
    // 配置p_buttonImage
    p_buttonImage->setFixedSize(128, 128);
    p_buttonImage->setAlignment(Qt::AlignCenter);
    // 判断纽扣图片是否存在
    QString imagepath(*p_configFilePath + "/" + *p_configFileName + IMAGE_TYPE);
    QFileInfo imagefileinfo(imagepath);
    if(imagefileinfo.isFile())
    {
        QImage button_image(imagepath);
        p_buttonImage->setPixmap(QPixmap::fromImage(button_image));
    }
    // 配置p_buttonName
    QFont font;
    font.setPointSize(14);
    font.setBold(false);
    font.setWeight(16);
    p_buttonName->setFont(font);
    p_buttonName->setFixedSize(128, 16);
    p_buttonName->setAlignment(Qt::AlignCenter);
    // 配置Label布局
    p_layout->setSpacing(5);
    p_layout->addWidget(p_buttonImage);
    p_layout->addWidget(p_buttonName);
    this->setLayout(p_layout);  // label加入到父对象
}

// [虚函数覆盖]事件过滤器函数
bool ButtonInfo::eventFilter(QObject* watched, QEvent* event)
{
    // 处理buttonImage-Label的鼠标单击事件
    if(watched == p_buttonImage)
    {
        if(event->type() == QEvent::MouseButtonPress)
        {
            emit SignalCmd_ButtonImageSelected(this);
            return true;
        }
        else
            return false;
    }
    else
    {
        return QWidget::eventFilter(watched, event);
    }
}
