#ifndef FrmInput_H
#define FrmInput_H
#include <QWidget>
#include <QTimer>
#include <QMouseEvent>
#include <QPushButton>
#include <QLabel>
#include <QLineEdit>
#include <QComboBox>
#include <QTextEdit>
#include <QPlainTextEdit>
#include <QTextBrowser>

namespace Ui
{
    class FrmInputWidget;
}

class FrmInput : public QWidget
{
    Q_OBJECT

public:
    ~FrmInput();
    // 静态的单例指针获取接口
    static FrmInput* GetInstance(QWidget* parent = nullptr);
    //初始化面板状态,包括字体大小
    void Init(const QString& position, const QString& style);

protected:
    //鼠标拖动事件（用于窗体移动）
    void mouseMoveEvent(QMouseEvent* event);
    //鼠标按下事件（用于窗体移动）
    void mousePressEvent(QMouseEvent* event);
    //鼠标松开事件（用于窗体移动）
    void mouseReleaseEvent(QMouseEvent*);

private slots:
    //焦点改变事件槽函数处理
    void SlotsWidgetFocusChanged(QWidget* oldWidget, QWidget* nowWidget);
    //输入法面板按键处理
    void SlotsButtonClicked();

private:
    // 私有化的构造函数，确保除了单例接口外，其他对象无法构造出系统管理类
    explicit FrmInput(QWidget* parent = nullptr);

private:
    static FrmInput* p_frmInput;
    Ui::FrmInputWidget *ui;

    qint32 deskWidth;                   // 桌面宽度
    qint32 deskHeight;                  // 桌面高度
    qint32 frmWidth;                    // 窗体宽度
    qint32 frmHeight;                   // 窗体高度
    QPoint mousePoint;                  // 鼠标拖动自定义标题栏时的坐标

    QString currentType;                // 当前输入法类型
    QString currentStyle;               // 当前输入法面板样式
    QString currentPosition;            // 当前输入法面板位置类型
    QString currentEditType;            // 当前焦点控件的类型

    QWidget* p_currentWidget;            // 当前焦点的对象
    QLineEdit* p_currentLineEdit;        // 当前焦点的单行文本框
    QTextEdit* p_currentTextEdit;        // 当前焦点的多行文本框
    QPlainTextEdit* p_currentPlain;      // 当前焦点的富文本框
    QTextBrowser* p_currentBrowser;      // 当前焦点的文本浏览框

    bool isFirst;                       // 是否首次加载
    bool mousePressed;                  // 鼠标是否按下

    void ShowPanel();                           // 显示输入法面板
    void InsertValue(const QString& value);     // 插入值到当前焦点控件
    void DeleteValue();                         // 删除当前焦点控件的一个字符
    void ChangeType(const QString& type);       // 改变输入法类型
    void InitForm();                            // 初始化窗体数据
    void InitButtonProperty();                  // 初始化属性
    void ChangeStyle();                         // 改变样式
    void SetStyleQss(const QString& topColor, const QString& bottomColor, const QString& borderColor, const QString& textColor);
};

#endif
