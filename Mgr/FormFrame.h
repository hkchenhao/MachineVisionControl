#ifndef FormFrame_H
#define FormFrame_H
#include <QWidget>
#include <QVBoxLayout>
#include <QStackedWidget>
#include <QDebug>

//尽量少直接包含其他的头文件，使用前置申明加快编译速度，减少后期维护的时间
class StartForm;
class MainForm;
class ButtonSelForm;
class StatisticalForm;

// 各个界面在QStackedWidget的索引号
typedef struct
{
  qint32 startform_id;
  qint32 mainform_id;
  qint32 buttonselform_id;
  qint32 statisticalform_id;
} FormIdStruct;

class FormFrame : public QWidget
{
    Q_OBJECT
public:
    ~FormFrame();
    // 静态的单例指针获取接口
    static FormFrame* GetInstance(QWidget* parent = nullptr)
    {
        static FormFrame instance_(parent);
        return &instance_;
    }
public:
    FormIdStruct formstacked_id_;      // Form窗口ID编号
    QStackedWidget* p_formstacked_;    // Form窗口管理器
    StartForm* p_startform_;           // Form Widget指针
    MainForm* p_mainform_;                  //
    ButtonSelForm* p_buttonselform_;        //
    StatisticalForm* p_statisticalform_;    //
private:
    // 私有化的构造函数，确保除了单例接口外，其他对象无法构造出系统管理类
    explicit FormFrame(QWidget* parent = nullptr);
};

#endif
