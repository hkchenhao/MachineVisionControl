#ifndef INFOCHECKRESFORM_H
#define INFOCHECKRESFORM_H
#include <QWidget>
#include <QMouseEvent>

namespace Ui
{
    class InfoCheckResWidget;
}

class InfoCheckResForm : public QWidget
{
    Q_OBJECT
public:
    explicit InfoCheckResForm(QWidget* parent = nullptr);
    ~InfoCheckResForm();
    Ui::InfoCheckResWidget* GetWidgetPtr() { return ui; }

signals:

public slots:

private slots:
    void on_pushButton_clicked();

protected:
    virtual void mousePressEvent(QMouseEvent *event);
    virtual void mouseMoveEvent(QMouseEvent *event);
    virtual void mouseReleaseEvent(QMouseEvent *event);

private:
    Ui::InfoCheckResWidget* ui;
    bool ismoveing;
    QPoint moveposition;
};

#endif
