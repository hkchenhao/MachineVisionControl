#include "FrmInput.h"
#include "ui_FrmInput.h"
#include "qdesktopwidget.h"
#include <QDebug>

// 静态变量初始化
FrmInput* FrmInput::p_frmInput = nullptr;

// [静态成员函数]静态的单例指针获取接口
FrmInput* FrmInput::GetInstance(QWidget* parent)
{
    if(p_frmInput == nullptr)
    {
        p_frmInput = new FrmInput(parent);
    }
    return p_frmInput;
}

// [构造函数与析构函数]
FrmInput::FrmInput(QWidget* parent) : QWidget(parent), ui(new Ui::FrmInputWidget)
{
    ui->setupUi(this);
    this->InitButtonProperty();
    this->InitForm();
    this->ChangeStyle();
}

FrmInput::~FrmInput()
{
    delete ui;
}

// [公有成员函数]初始化
// position-> control:显示在对应输入框的正下方 bottom:填充显示在底部  center:窗体居中显示
// style-> blue:淡蓝色  dev:dev风格  black:黑色  brown:灰黑色  lightgray:浅灰色  darkgray:深灰色  gray:灰色  silvery:银色
void FrmInput::Init(const QString& position, const QString& style)
{
    this->currentPosition = position;
    this->currentStyle = style;
    this->ChangeStyle();
}

// [虚函数]鼠标按下/拖动/释放事件处理（用于窗体移动）
void FrmInput::mousePressEvent(QMouseEvent* event)
{
    if (event->button() == Qt::LeftButton)
    {
        mousePressed = true;
        mousePoint = event->globalPos() - this->pos();
        event->accept();
    }
}

void FrmInput::mouseMoveEvent(QMouseEvent* event)
{
    if (mousePressed && (event->buttons() && Qt::LeftButton))
    {
        this->move(event->globalPos() - mousePoint);
        event->accept();
    }
}

void FrmInput::mouseReleaseEvent(QMouseEvent*)
{
    mousePressed = false;
}

// [槽函数]按键处理
void FrmInput::SlotsButtonClicked()
{
    //如果当前焦点控件类型为空,则返回不需要继续处理
    if (currentEditType == "")
    {
        return;
    }
    // 获取按键类型
    QPushButton* buttton = static_cast<QPushButton*>(sender());
    QString objectName = buttton->objectName();
    // 按键类型判断
    if (objectName == "btnType")
    {
        if (currentType == "min")
            currentType = "max";
        else
            currentType = "min";
        ChangeType(currentType);
    }
    else if (objectName == "btnDelete")
    {
        DeleteValue();
    }
    else if (objectName == "btnClose")
    {
        if(p_currentLineEdit)
            p_currentLineEdit->clearFocus();
        this->setVisible(false);
    }
    else if (objectName == "btnSpace")
    {
        InsertValue(" ");
    }
    else
    {
        // 获取按键上的字符
        QString value = buttton->text();
        //如果是&按钮，因为对应&被过滤,所以真实的text为去除前面一个&字符
        if (objectName == "btnOther7")
        {
            value = value.right(1);
        }
        // 显示字符
        InsertValue(value);
    }
}

// [槽函数]窗体焦点改变
void FrmInput::SlotsWidgetFocusChanged(QWidget* oldWidget, QWidget* nowWidget)
{
    //qDebug() << "foucs changed!";
    if (nowWidget != nullptr && !this->isAncestorOf(nowWidget))
    {
        //在Qt5和linux系统中(嵌入式linux除外),当输入法面板关闭时,焦点会变成无,然后焦点会再次移到焦点控件处
        //这样导致输入法面板的关闭按钮不起作用,关闭后马上有控件获取焦点又显示.
        //为此,增加判断,当焦点是从有对象转为无对象再转为有对象时不要显示.
        //这里又要多一个判断,万一首个窗体的第一个焦点就是落在可输入的对象中,则要过滤掉
        #ifndef __arm__
            if (oldWidget == nullptr && !isFirst) { return; }
        #endif

        // 控件widget判断
        isFirst = false;
        if (nowWidget->inherits("QLineEdit"))
        {
            p_currentLineEdit = (QLineEdit*)nowWidget;
            currentEditType = "QLineEdit";
            ShowPanel();
        }
        else if (nowWidget->inherits("QTextEdit"))
        {
            p_currentTextEdit = (QTextEdit*)nowWidget;
            currentEditType = "QTextEdit";
            ShowPanel();
        }
        else if (nowWidget->inherits("QPlainTextEdit"))
        {
            p_currentPlain = (QPlainTextEdit*)nowWidget;
            currentEditType = "QPlainTextEdit";
            ShowPanel();
        }
        else if (nowWidget->inherits("QTextBrowser"))
        {
            p_currentBrowser = (QTextBrowser*)nowWidget;
            currentEditType = "QTextBrowser";
            ShowPanel();
        }
        else if (nowWidget->inherits("QComboBox"))
        {
            QComboBox *cbox = (QComboBox*)nowWidget;
            //只有当下拉选择框处于编辑模式才可以输入
            if (cbox->isEditable())
            {
                p_currentLineEdit = cbox->lineEdit() ;
                currentEditType = "QLineEdit";
                ShowPanel();
            }
        }
        else if (nowWidget->inherits("QSpinBox") || nowWidget->inherits("QDoubleSpinBox") || nowWidget->inherits("QDateEdit") ||\
                 nowWidget->inherits("QTimeEdit") || nowWidget->inherits("QDateTimeEdit"))
        {
            p_currentWidget = nowWidget;
            currentEditType = "QWidget";
            ShowPanel();
        }
        else
        {
            //需要将输入法切换到最初的原始状态--小写,同时将之前的对象指针置为零
            p_currentWidget = nullptr;
            p_currentLineEdit = nullptr;
            p_currentTextEdit = nullptr;
            p_currentPlain = nullptr;
            p_currentBrowser = nullptr;
            currentEditType = "";
            currentType = "min";
            ChangeType(currentType);
            this->setVisible(false);
            return;
        }

        //根据用户选择的输入法位置设置-居中显示-底部填充-显示在输入框正下方
        if (currentPosition == "center")
        {
            QPoint pos = QPoint(deskWidth / 2 - frmWidth / 2, deskHeight / 2 - frmHeight / 2);
            this->setGeometry(pos.x(), pos.y(), frmWidth, frmHeight);
        }
        else if (currentPosition == "bottom")
        {
            this->setGeometry(0, deskHeight - frmHeight, deskWidth, frmHeight);
        }
        else if (currentPosition == "control")
        {
            QRect rect = nowWidget->rect();
            QPoint pos = QPoint(rect.left(), rect.bottom() + 2);
            pos = nowWidget->mapToGlobal(pos);
            this->setGeometry(pos.x(), pos.y(), frmWidth, frmHeight);
        }
    }
}

// [私有成员函数]显示输入法面板
void FrmInput::ShowPanel()
{
    this->setVisible(true);
    qint32 width = ui->btn0->width();
    width = width > 40 ? width : 40;
    ui->btnClose->setMinimumWidth(width);
    ui->btnClose->setMaximumWidth(width);
}

// [私有成员函数]插入值到当前焦点控件
void FrmInput::InsertValue(const QString& value)
{
    if (currentEditType == "QLineEdit")
    {
        p_currentLineEdit->insert(value);
    }
    else if (currentEditType == "QTextEdit")
    {
        p_currentTextEdit->insertPlainText(value);
    }
    else if (currentEditType == "QPlainTextEdit")
    {
        p_currentPlain->insertPlainText(value);
    }
    else if (currentEditType == "QTextBrowser")
    {
        p_currentBrowser->insertPlainText(value);
    }
    else if (currentEditType == "QWidget")
    {
        QKeyEvent keyPress(QEvent::KeyPress, 0, Qt::NoModifier, QString(value));
        QApplication::sendEvent(p_currentWidget, &keyPress);
    }
}

// [私有成员函数]从当前焦点控件删除值
void FrmInput::DeleteValue()
{
    if (currentEditType == "QLineEdit")
    {
        p_currentLineEdit->backspace();
    }
    else if (currentEditType == "QTextEdit")
    {
        //获取当前QTextEdit光标,如果光标有选中,则移除选中字符,否则删除光标前一个字符
        QTextCursor cursor = p_currentTextEdit->textCursor();
        if(cursor.hasSelection())
            cursor.removeSelectedText();
        else
            cursor.deletePreviousChar();
    }
    else if (currentEditType == "QPlainTextEdit")
    {
        //获取当前QTextEdit光标,如果光标有选中,则移除选中字符,否则删除光标前一个字符
        QTextCursor cursor = p_currentPlain->textCursor();
        if(cursor.hasSelection())
            cursor.removeSelectedText();
        else
            cursor.deletePreviousChar();
    }
    else if (currentEditType == "QTextBrowser")
    {
        //获取当前QTextEdit光标,如果光标有选中,则移除选中字符,否则删除光标前一个字符
        QTextCursor cursor = p_currentBrowser->textCursor();
        if(cursor.hasSelection())
            cursor.removeSelectedText();
        else
            cursor.deletePreviousChar();
    }
    else if (currentEditType == "QWidget")
    {
        QKeyEvent keyPress(QEvent::KeyPress, Qt::Key_Delete, Qt::NoModifier, QString());
        QApplication::sendEvent(p_currentWidget, &keyPress);
    }
}

// [私有成员函数]改变输入法模式
void FrmInput::ChangeType(const QString& type)
{
    if (type == "max")
    {
        // 字母改变为大写
        QList<QPushButton*> buttonlist = this->findChildren<QPushButton*>();
        foreach (QPushButton* button, buttonlist)
        {
            if (button->property("btnLetter").toBool())
                button->setText(button->text().toUpper());
        }
        ui->btnType->setText("大写");
        ui->labInfo->setText("当前输入法--大写");
    }
    else
    {
        // 字母改变为小写
        QList<QPushButton*> buttonlist = this->findChildren<QPushButton*>();
        foreach (QPushButton* button, buttonlist)
        {
            if (button->property("btnLetter").toBool())
                button->setText(button->text().toLower());
        }
        ui->btnType->setText("小写");
        ui->labInfo->setText("当前输入法--小写");
    }
}

// [私有成员函数]初始化窗体
void FrmInput::InitForm()
{
    this->setWindowFlags(Qt::Tool | Qt::WindowStaysOnTopHint | Qt::FramelessWindowHint);
    // 变量初始化
    QDesktopWidget widget;
    deskWidth = widget.availableGeometry().width();
    deskHeight = widget.availableGeometry().height();
    frmWidth = this->width();
    frmHeight = this->height();
    p_currentWidget = nullptr;
    p_currentLineEdit = nullptr;
    p_currentTextEdit = nullptr;
    p_currentPlain = nullptr;
    p_currentBrowser = nullptr;
    currentEditType = "";
    currentPosition = "";
    currentStyle = "";
    currentType = "min";
    mousePressed = false;
    isFirst = true;
    // 按键信号绑定
    QList<QPushButton*> buttonlist = this->findChildren<QPushButton*>();
    foreach (QPushButton* button, buttonlist)
    {
        QObject::connect(button, &QPushButton::clicked, this, &FrmInput::SlotsButtonClicked);
    }
    // 绑定全局改变焦点信号槽
    QObject::connect(qApp, &QApplication::focusChanged, this, &FrmInput::SlotsWidgetFocusChanged);
}

// [私有成员函数]初始化按键属性
void FrmInput::InitButtonProperty()
{
    // 设置属性为其他字符按键
    ui->btnOther1->setProperty("btnOther", true);
    ui->btnOther2->setProperty("btnOther", true);
    ui->btnOther3->setProperty("btnOther", true);
    ui->btnOther4->setProperty("btnOther", true);
    ui->btnOther5->setProperty("btnOther", true);
    ui->btnOther6->setProperty("btnOther", true);
    ui->btnOther7->setProperty("btnOther", true);
    ui->btnOther8->setProperty("btnOther", true);
    ui->btnOther9->setProperty("btnOther", true);
    ui->btnOther10->setProperty("btnOther", true);
    ui->btnOther11->setProperty("btnOther", true);
    ui->btnOther12->setProperty("btnOther", true);
    ui->btnOther13->setProperty("btnOther", true);
    ui->btnOther14->setProperty("btnOther", true);
    ui->btnOther15->setProperty("btnOther", true);
    ui->btnOther16->setProperty("btnOther", true);
    ui->btnOther17->setProperty("btnOther", true);
    ui->btnOther18->setProperty("btnOther", true);
    ui->btnOther19->setProperty("btnOther", true);
    ui->btnOther20->setProperty("btnOther", true);
    ui->btnOther21->setProperty("btnOther", true);
    ui->btnDot->setProperty("btnOther", true);
    ui->btnSpace->setProperty("btnOther", true);
    ui->btnDelete->setProperty("btnOther", true);
    // 设置属性为数字按键
    ui->btn0->setProperty("btnNum", true);
    ui->btn1->setProperty("btnNum", true);
    ui->btn2->setProperty("btnNum", true);
    ui->btn3->setProperty("btnNum", true);
    ui->btn4->setProperty("btnNum", true);
    ui->btn5->setProperty("btnNum", true);
    ui->btn6->setProperty("btnNum", true);
    ui->btn7->setProperty("btnNum", true);
    ui->btn8->setProperty("btnNum", true);
    ui->btn9->setProperty("btnNum", true);
    ui->btn00->setProperty("btnNum", true);
    // 设置属性为字母按键
    ui->btna->setProperty("btnLetter", true);
    ui->btnb->setProperty("btnLetter", true);
    ui->btnc->setProperty("btnLetter", true);
    ui->btnd->setProperty("btnLetter", true);
    ui->btne->setProperty("btnLetter", true);
    ui->btnf->setProperty("btnLetter", true);
    ui->btng->setProperty("btnLetter", true);
    ui->btnh->setProperty("btnLetter", true);
    ui->btni->setProperty("btnLetter", true);
    ui->btnj->setProperty("btnLetter", true);
    ui->btnk->setProperty("btnLetter", true);
    ui->btnl->setProperty("btnLetter", true);
    ui->btnm->setProperty("btnLetter", true);
    ui->btnn->setProperty("btnLetter", true);
    ui->btno->setProperty("btnLetter", true);
    ui->btnp->setProperty("btnLetter", true);
    ui->btnq->setProperty("btnLetter", true);
    ui->btnr->setProperty("btnLetter", true);
    ui->btns->setProperty("btnLetter", true);
    ui->btnt->setProperty("btnLetter", true);
    ui->btnu->setProperty("btnLetter", true);
    ui->btnv->setProperty("btnLetter", true);
    ui->btnw->setProperty("btnLetter", true);
    ui->btnx->setProperty("btnLetter", true);
    ui->btny->setProperty("btnLetter", true);
    ui->btnz->setProperty("btnLetter", true);
}

// [私有成员函数]改变样式
void FrmInput::ChangeStyle()
{
    if (currentStyle == "blue")
        SetStyleQss("#DEF0FE", "#C0DEF6", "#C0DCF2", "#386487");
    else if (currentStyle == "dev")
        SetStyleQss("#C0D3EB", "#BCCFE7", "#B4C2D7", "#324C6C");
    else if (currentStyle == "gray")
        SetStyleQss("#E4E4E4", "#A2A2A2", "#A9A9A9", "#000000");
    else if (currentStyle == "lightgray")
        SetStyleQss("#EEEEEE", "#E5E5E5", "#D4D0C8", "#6F6F6F");
    else if (currentStyle == "darkgray")
        SetStyleQss("#D8D9DE", "#C8C8D0", "#A9ACB5", "#5D5C6C");
    else if (currentStyle == "black")
        SetStyleQss("#4D4D4D", "#292929", "#D9D9D9", "#CACAD0");
    else if (currentStyle == "brown")
        SetStyleQss("#667481", "#566373", "#C2CCD8", "#E7ECF0");
    else if (currentStyle == "silvery")
        SetStyleQss("#E1E4E6", "#CCD3D9", "#B2B6B9", "#000000");
}

void FrmInput::SetStyleQss(const QString& topColor, const QString& bottomColor, const QString& borderColor, const QString& textColor)
{
    QStringList qss;
    qss.append(QString("QWidget#FrmInputWidget{background:qlineargradient(spread:pad,x1:0,y1:0,x2:0,y2:1,stop:0 %1,stop:1 %2);}")
               .arg(topColor).arg(bottomColor));
    qss.append("QPushButton{padding:5px;border-radius:3px;}");
    qss.append(QString("QPushButton:hover{background:qlineargradient(spread:pad,x1:0,y1:0,x2:0,y2:1,stop:0 %1,stop:1 %2);}")
               .arg(topColor).arg(bottomColor));
    qss.append(QString("QLabel,QPushButton{color:%1;}").arg(textColor));
    qss.append(QString("QPushButton{border:1px solid %1;}")
               .arg(borderColor));
    qss.append(QString("QLineEdit{border:1px solid %1;border-radius:5px;padding:2px;background:none;selection-background-color:%2;selection-color:%3;}")
               .arg(borderColor).arg(bottomColor).arg(topColor));
    this->setStyleSheet(qss.join(""));
}

