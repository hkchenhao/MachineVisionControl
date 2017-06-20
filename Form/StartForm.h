#ifndef STARTFORM_H
#define STARTFORM_H
#include <QWidget>

namespace Ui
{
    class StartWidget;
}

class StartForm : public QWidget
{
    Q_OBJECT
public:
    explicit StartForm(QWidget* parent = nullptr);
    ~StartForm();
private slots:
    // Widgets槽函数
    void on_comboBox_UserName_currentIndexChanged(int index);
    void on_lineEdit_Passwd_textEdited(const QString& arg1);
    void on_pushButton_Load_clicked();

private:
    Ui::StartWidget* ui;
};

#endif
